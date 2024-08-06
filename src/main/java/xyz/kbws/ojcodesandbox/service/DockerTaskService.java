package xyz.kbws.ojcodesandbox.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kbws.ojcodesandbox.model.ExecuteMessage;
import xyz.kbws.ojcodesandbox.utils.DockerContainerPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author kbws
 * @date 2024/7/28
 * @description:
 */
@Service
public class DockerTaskService {
    private final DockerContainerPool containerPool;
    private final ExecutorService executor;

    @Autowired
    public DockerTaskService(DockerContainerPool containerPool) {
        this.containerPool = containerPool;
        this.executor = Executors.newFixedThreadPool(containerPool.getMaxPoolSize());
    }

    public Future<List<ExecuteMessage>> submitTask(String javaCode, List<String> inputParams) {
        return executor.submit(() -> {
            String containerId = null;
            try {
                containerId = containerPool.getContainer();
                return manageContainer(containerId, javaCode, inputParams);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return Arrays.asList(createErrorMessage("Error managing container " + containerId, e.getMessage()));
            } finally {
                if (containerId != null) {
                    containerPool.releaseContainer(containerId);
                }
            }
        });
    }

    private List<ExecuteMessage> manageContainer(String containerId, String javaCode, List<String> inputParams) {
        DockerClient dockerClient = containerPool.getDockerClient();
        try {
            String script = "echo \"" + javaCode.replace("\"", "\\\"") + "\" > /app/Main.java && javac /app/Main.java";
            dockerClient.execStartCmd(dockerClient.execCreateCmd(containerId).withCmd("sh", "-c", script).exec().getId())
                    .exec(new ExecStartResultCallback(System.out, System.err))
                    .awaitCompletion();

            List<ExecuteMessage> executeMessageList = new ArrayList<>();
            for (String inputArgs : inputParams) {
                String[] cmdArray = {"java", "-cp", "/app", "Main"};
                ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                        .withCmd(cmdArray)
                        .withAttachStderr(true)
                        .withAttachStdin(true)
                        .withAttachStdout(true)
                        .exec();

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
                ByteArrayInputStream input = new ByteArrayInputStream(inputArgs.getBytes());

                ExecStartResultCallback resultCallback = new ExecStartResultCallback(output, errorOutput) {
                    @SneakyThrows
                    @Override
                    public void onNext(Frame frame) {
                        if (frame.getStreamType() == StreamType.STDOUT) {
                            output.write(frame.getPayload());
                        } else if (frame.getStreamType() == StreamType.STDERR) {
                            errorOutput.write(frame.getPayload());
                        }
                    }
                };

                dockerClient.execStartCmd(execCreateCmdResponse.getId())
                        .withDetach(false)
                        .withTty(true)
                        .exec(resultCallback);

                resultCallback.awaitCompletion();

                ExecuteMessage executeMessage = new ExecuteMessage();
                executeMessage.setMessage(output.toString());
                executeMessage.setErrorMessage(errorOutput.toString());
                executeMessageList.add(executeMessage);
            }
            return executeMessageList;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return Arrays.asList(createErrorMessage("Error managing container " + containerId, e.getMessage()));
        }
    }

    private ExecuteMessage createErrorMessage(String message, String errorMessage) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        executeMessage.setMessage(message);
        executeMessage.setErrorMessage(errorMessage);
        return executeMessage;
    }

    public void shutdown() {
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        containerPool.shutdown();
    }
}
