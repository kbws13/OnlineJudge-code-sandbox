package xyz.kbws.ojcodesandbox.controller;

import org.springframework.web.bind.annotation.*;
import xyz.kbws.ojcodesandbox.model.enums.QuestionSubmitStatusEnum;
import xyz.kbws.ojcodesandbox.model.enums.SupportLanguageEnum;
import xyz.kbws.ojcodesandbox.service.java.JavaDockerCodeSandBox;
import xyz.kbws.ojcodesandbox.service.java.JavaNativeCodeSandbox;
import xyz.kbws.ojcodesandbox.model.ExecuteCodeRequest;
import xyz.kbws.ojcodesandbox.model.ExecuteCodeResponse;
import xyz.kbws.ojcodesandbox.service.python3.Python3Native3CodeSandbox;

import static xyz.kbws.ojcodesandbox.constants.AuthRequest.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kbws
 * @date 2023/11/2
 * @description:
 */
@RestController
@RequestMapping("/codesandbox")
public class MainController {

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    @Resource
    private JavaDockerCodeSandBox javaDockerCodeSandBox;

    @Resource
    private Python3Native3CodeSandbox python3NativeCodeSandbox;

    @GetMapping("/health")
    public String healthCheck(){
        return "ok";
    }

    /**
     * 系统自我介绍
     *
     * @return
     */
    @GetMapping("/intro")
    public String selfIntroduction()
    {
        return "您好，我是由【空白无上】开发的代码沙箱，可完成代码的运行和结果返回。\n" +
                "目前我支持的语言有"+SupportLanguageEnum.getValues()+"\n"+
                "如果对您有帮助，欢迎来我主页点个关注~\n" +
                "作者Github："+"https://github.com/kbws13";
    }

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    @PostMapping("/run")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest,
                                           HttpServletRequest request, HttpServletResponse response) {
        //String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        //// 基本的认证
        //if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
        //    response.setStatus(403);
        //    return new ExecuteCodeResponse(null, "身份校验失败！", QuestionSubmitStatusEnum.FAILED.getValue(), null);
        //}
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        String language = executeCodeRequest.getLanguage();
        if (SupportLanguageEnum.JAVA.getValue().equals(language)) {
            return javaNativeCodeSandbox.executeCode(executeCodeRequest);
        }
        else if (SupportLanguageEnum.PYTHON3.getValue().equals(language)) {
            return python3NativeCodeSandbox.executeCode(executeCodeRequest);
        }
        else {
            return new ExecuteCodeResponse(null, "不支持的编程语言：" + language + "；当前仅支持：" + SupportLanguageEnum.getValues(), QuestionSubmitStatusEnum.FAILED.getValue(), null);
        }
    }
}
