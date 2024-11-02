package xyz.kbws.executor;

import cn.hutool.core.io.FileUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.DockerClientBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author kbws
 * @date 2024/11/2
 * @description:
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "codesandbox.config")
@Configuration
public class DockerSandBox {

    private static final DockerClient DOCKER_CLIENT = DockerClientBuilder.getInstance().build();

    /**
     * 代码沙箱的镜像，Dockerfile 构建的镜像名，默认为 codesandbox:latest
     */
    private String imageName = "codesandbox:latest";

    /**
     * 内存限制，单位为字节，默认为 1024 * 1024 * 60MB
     */
    private long memoryLimit = 1024 * 1024 * 60;

    private long memorySwap = 0;

    /**
     * 最大可执行的 CPU 数
     */
    private long cpuCount = 1;

    private long timeoutLimit = 1;

    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * 创建容器
     */
    private String createContainer(String codeFile) {
        CreateContainerCmd createContainerCmd = DOCKER_CLIENT.createContainerCmd(imageName);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(memoryLimit);
        hostConfig.withMemorySwap(memorySwap);
        hostConfig.withCpuCount(cpuCount);

        CreateContainerResponse createContainerResponse = createContainerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .exec();

        // 启动容器
        String containerId = createContainerResponse.getId();
        DOCKER_CLIENT.startContainerCmd(containerId).exec();

        // 将代码复制到容器中
        DOCKER_CLIENT.copyArchiveToContainerCmd(containerId)
                .withHostResource(codeFile)
                .withRemotePath("/box")
                .exec();
        return containerId;
    }

    /**
     * 执行命令
     */
    private ExecuteMessage execCmd(String containerId, String[] cmd) {
        // 正常返回信息
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        // 错误信息
        ByteArrayOutputStream errorResultStream = new ByteArrayOutputStream();

        // 结果
        final boolean[] result = {true};
        final boolean[] timeout = {true};
        try (ResultCallback.Adapter<Frame> frameAdapter = new ResultCallback.Adapter<Frame>(){

            @Override
            public void onComplete(){
                // 是否超时
                timeout[0] = false;
                super.onComplete();
            }

            @Override
            public void onNext(Frame frame) {
                StreamType streamType = frame.getStreamType();
                byte[] payLoad = frame.getPayload();
                if (StreamType.STDERR.equals(streamType)) {
                    try {
                        result[0] = false;
                        errorResultStream.write(payLoad);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        resultStream.write(payLoad);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                super.onNext(frame);
            }
        }) {
            ExecCreateCmdResponse execCreateCmdResponse = DOCKER_CLIENT.execCreateCmd(containerId)
                    .withCmd(cmd)
                    .withAttachStdin(true)
                    .withAttachStderr(true)
                    .withAttachStdout(true)
                    .exec();
            String execId = execCreateCmdResponse.getId();
            DOCKER_CLIENT.execStartCmd(execId).exec(frameAdapter).awaitCompletion(timeoutLimit, timeUnit);

            // 超时
            if (timeout[0]) {
                return ExecuteMessage
                        .builder()
                        .success(false)
                        .errorMessage("执行超时")
                        .build();
            }

            return ExecuteMessage
                    .builder()
                    .success(result[0])
                    .messages(resultStream.toString())
                    .errorMessage(errorResultStream.toString())
                    .build();
        }catch (IOException | InterruptedException e) {
            log.info(e.getMessage());
            return ExecuteMessage
                    .builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * 清理文件和容器
     */
    private static void cleanFileAndContainer(String userCodePath, String containerId) {
        CompletableFuture.runAsync(() -> {
            // 清理临时目录
            FileUtil.del(userCodePath);

            // 关闭并删除容器
            DOCKER_CLIENT.stopContainerCmd(containerId).exec();
            DOCKER_CLIENT.removeContainerCmd(containerId).exec();
        });
    }

    /**
     * 执行代码
     */
    public ExecuteMessage execute(LanguageCmdEnum languageCmdEnum, String code) {
        // 写入文件
        String userDir = System.getProperty("user.dir");
        String language = languageCmdEnum.getLanguage();
        String globalCodePathName = userDir + File.separator + "tempCode" + File.separator + language;
        // 判断全局代码目录是否存在，没有则新建
        File globalCodePath = new File(globalCodePathName);
        if (!globalCodePath.exists()) {
            boolean mkdir = globalCodePath.mkdirs();
            if (!mkdir) {
                log.info("创建全局代码目录失败");
            }
        }

        // 把用户代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + languageCmdEnum.getSaveFileName();
        FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        String containerId = createContainer(userCodePath);

        // 编译代码
        String[] compileCmd = languageCmdEnum.getCompileCmd();
        ExecuteMessage executeMessage;

        // 不为空则需要编译
        if (compileCmd != null) {
            executeMessage = execCmd(containerId, compileCmd);
            log.info("编译完成...");

            // 编译错误
            if (!executeMessage.isSuccess()) {
                // 清除文件
                cleanFileAndContainer(userCodePath, containerId);
                return executeMessage;
            }
        }

        executeMessage = execCmd(containerId, languageCmdEnum.getRunCmd());
        log.info("运行完成...");

        // 清除文件
        cleanFileAndContainer(userCodePath, containerId);
        return executeMessage;
    }
}
