package xyz.kbws.ojcodesandbox.service.java;

import org.springframework.stereotype.Component;
import xyz.kbws.ojcodesandbox.model.ExecuteCodeRequest;
import xyz.kbws.ojcodesandbox.model.ExecuteCodeResponse;
import xyz.kbws.ojcodesandbox.service.java.JavaCodeSandboxTemplate;

/**
 * @author kbws
 * @date 2023/11/11
 * @description: Java原生代码沙箱实现（复用模板）
 */
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
