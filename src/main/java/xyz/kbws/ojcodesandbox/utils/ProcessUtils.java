package xyz.kbws.ojcodesandbox.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;
import xyz.kbws.ojcodesandbox.model.ExecuteMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kbws
 * @date 2023/11/3
 * @description: 进程工具类
 */
@Slf4j
public class ProcessUtils {
    /**
     * 执行进程并获取信息
     *
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage getProcessMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 等待程序执行，获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            // 正常退出
            if (exitValue != 0) {
                executeMessage.setErrorMessage(getProcessOutput(runProcess.getErrorStream()));
            } else {
                executeMessage.setMessage(getProcessOutput(runProcess.getInputStream()));
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        } catch (Exception e) {
            log.error(opName + "失败：{}", e.toString());
        }
        return executeMessage;
    }

    public static ExecuteMessage getAcmProcessMessage(Process runProcess, String input) throws IOException {
        ExecuteMessage executeMessage = new ExecuteMessage();

        StringReader inputReader = new StringReader(input);
        BufferedReader inputBufferReader = new BufferedReader(inputReader);


        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 输入（模拟控制台输入）
        PrintWriter consoleInput = new PrintWriter(runProcess.getOutputStream());
        String line;
        while ((line = inputBufferReader.readLine()) != null) {
            consoleInput.println(line);
            consoleInput.flush();
        }
        consoleInput.close();

        // 获取输出
        BufferedReader userCodeOutput = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
        List<String> outputList = new ArrayList<>();
        String outputLine;
        while ((outputLine = userCodeOutput.readLine()) != null) {
            outputList.add(outputLine);
        }
        userCodeOutput.close();

        //获取错误输出
        BufferedReader errorOutput = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
        List<String> errorList = new ArrayList<>();
        String errorLine;
        while ((errorLine = errorOutput.readLine()) != null) {
            errorList.add(errorLine);
        }
        errorOutput.close();

        stopWatch.stop();
        executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        executeMessage.setMessage(StringUtils.join(outputList, "\n"));
        executeMessage.setErrorMessage(StringUtils.join(errorList, "\n"));
        runProcess.destroy();

        return executeMessage;
    }

    /**
     * 获取某个流的输出
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String getProcessOutput(InputStream inputStream) throws IOException {
        // 分批获取进程的正常输出
        // Linux 写法
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        // Windows 写法
        // BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
        StringBuilder outputSb = new StringBuilder();
        // 逐行读取
        String outputLine;
        while ((outputLine = bufferedReader.readLine()) != null) {
            outputSb.append(outputLine).append("\n");
        }
        bufferedReader.close();
        return outputSb.toString();
    }
}
