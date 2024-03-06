package xyz.kbws.ojcodesandbox.service.python3;

import org.springframework.stereotype.Component;
import xyz.kbws.ojcodesandbox.model.ExecuteCodeRequest;
import xyz.kbws.ojcodesandbox.model.ExecuteCodeResponse;

/**
 * @author kbws
 * @date 2024/2/29
 * @description: Python3 原生代码沙箱实现
 */
@Component
public class Python3Native3CodeSandbox extends Python3CodeSandboxTemplate{
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
