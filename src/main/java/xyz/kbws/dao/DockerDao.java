package xyz.kbws.dao;

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
import xyz.kbws.entity.ExecuteMessage;
import xyz.kbws.executor.ContainerInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author kbws
 * @date 2024/11/2
 * @description: 所有对 Docker 的操作
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "codesandbox.config")
public class DockerDao {

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

    private static final DockerClient DOCKER_CLIENT = DockerClientBuilder.getInstance().build();

    /**
     * 执行命令
     * @param containerId 容器 Id
     * @param cmd CMD
     * @return {@link ExecuteMessage}
     */
    public ExecuteMessage execCmd(String containerId, String[] cmd) {
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
     * 创建容器
     */
    public ContainerInfo createContainer(String codePath) {
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

        String containerId = createContainerResponse.getId();
        log.info("containerId: {}", containerId);

        // 启动容器
        DOCKER_CLIENT.startContainerCmd(containerId).exec();

        return ContainerInfo
                .builder()
                .containerId(containerId)
                .codePathName(codePath)
                .lastActivityTime(System.currentTimeMillis())
                .build();
    }

    /**
     * 复制文件到容器中
     * @param containerId 容器 id
     * @param codeFile 代码文件
     */
    public void copyFileToContainer(String containerId, String codeFile) {
        DOCKER_CLIENT.copyArchiveToContainerCmd(containerId)
                .withHostResource(codeFile)
                .withRemotePath("/box")
                .exec();
    }


    public void cleanContainer(String containerId) {
        DOCKER_CLIENT.stopContainerCmd(containerId).exec();
        DOCKER_CLIENT.removeContainerCmd(containerId).exec();
    }
}
