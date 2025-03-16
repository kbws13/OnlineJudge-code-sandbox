package xyz.kbws;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.kbws.entity.ExecuteMessage;
import xyz.kbws.executor.ContainerPoolExecutor;
import xyz.kbws.dao.DockerDao;
import xyz.kbws.enums.LanguageCmdEnum;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author kbws
 * @date 2024/11/2
 * @description:
 */
@Slf4j
@Service
public class DockerSandBox {

    @Resource
    private DockerDao dockerDao;

    @Resource
    private ContainerPoolExecutor containerPoolExecutor;


    public ExecuteMessage execute(LanguageCmdEnum languageCmdEnum, String code) {
        return containerPoolExecutor.run(containerInfo -> {
            try {
                String containerId = containerInfo.getContainerId();
                String codePathName = containerInfo.getCodePathName();
                String condeFileName = codePathName + File.separator + languageCmdEnum.getSaveFileName();

                FileUtil.writeString(code, condeFileName, StandardCharsets.UTF_8);

                dockerDao.copyFileToContainer(containerId, condeFileName);

                // 编译代码
                String[] compileCmd = languageCmdEnum.getCompileCmd();
                ExecuteMessage executeMessage;

                // 不为空表示需要编译
                if (compileCmd != null) {
                    executeMessage = dockerDao.execCmd(containerId, compileCmd);
                    log.info("compile complete...");
                    // 编译错误
                    if (!executeMessage.isSuccess()) {
                        return executeMessage;
                    }
                }

                String[] runCmd = languageCmdEnum.getRunCmd();
                executeMessage = dockerDao.execCmd(containerId, runCmd);
                log.info("run complete...");
                return executeMessage;
            } catch (Exception e) {
                return ExecuteMessage
                        .builder()
                        .success(false)
                        .errorMessage(e.getMessage())
                        .build();
            }
        });
    }
}
