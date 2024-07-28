package xyz.kbws.ojcodesandbox.service.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.kbws.ojcodesandbox.model.ExecuteMessage;
import xyz.kbws.ojcodesandbox.service.DockerTaskService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author kbws
 * @date 2023/11/3
 * @description: Java使用Docker实现代码沙箱（复用模板）
 */
@Component
public class JavaDockerCodeSandBox extends JavaCodeSandboxTemplate {

    private final DockerTaskService dockerTaskService;

    @Autowired
    public JavaDockerCodeSandBox(DockerTaskService dockerTaskService) {
        this.dockerTaskService = dockerTaskService;
    }


    /**
     * 创建容器，把文件复制到容器内
     * @param userCodeFile
     * @param inputList
     * @return
     */
    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        try {
            String javaCode = new String(java.nio.file.Files.readAllBytes(userCodeFile.toPath()), StandardCharsets.UTF_8);
            Future<List<ExecuteMessage>> future = dockerTaskService.submitTask(javaCode, inputList);
            return future.get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            ExecuteMessage errorMessage = new ExecuteMessage();
            errorMessage.setMessage("Error executing code");
            errorMessage.setErrorMessage(e.getMessage());
            return Arrays.asList(errorMessage);
        }
    }

}
