package xyz.kbws.ojcodesandbox;


import xyz.kbws.ojcodesandbox.model.ExecuteCodeRequest;
import xyz.kbws.ojcodesandbox.model.ExecuteCodeResponse;

/**
 * @author kbws
 * @date 2023/10/31
 * @description:
 */
public interface CodeSandBox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
