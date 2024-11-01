package xyz.kbws;

import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.jupiter.api.Test;
import xyz.kbws.executor.DockerSandBox;
import xyz.kbws.executor.ExecuteMessage;
import xyz.kbws.executor.LanguageCmdEnum;

import java.nio.charset.StandardCharsets;

/**
 * @author kbws
 * @date 2024/11/2
 * @description:
 */
public class DockerSandBoxTest {

    @Test
    void testJava() {
        String code = ResourceUtil.readStr("languageCode/Main.java", StandardCharsets.UTF_8);
        ExecuteMessage execute = DockerSandBox.execute(LanguageCmdEnum.JAVA, code);
        System.out.println(execute);
    }

    @Test
    void testCpp() throws InterruptedException {
        String code = ResourceUtil.readStr("languageCode/main.cpp", StandardCharsets.UTF_8);
        ExecuteMessage execute = DockerSandBox.execute(LanguageCmdEnum.CPP, code);
        System.out.println(execute);
    }

    @Test
    void testC() throws InterruptedException {
        String code = ResourceUtil.readStr("languageCode/main.c", StandardCharsets.UTF_8);
        ExecuteMessage execute = DockerSandBox.execute(LanguageCmdEnum.C, code);
        System.out.println(execute);
    }

    @Test
    void testPython() throws InterruptedException {
        String code = ResourceUtil.readStr("languageCode/main.py", StandardCharsets.UTF_8);
        ExecuteMessage execute = DockerSandBox.execute(LanguageCmdEnum.PYTHON3, code);
        System.out.println(execute);
    }

    @Test
    void testJs() throws InterruptedException {
        String code = ResourceUtil.readStr("languageCode/main.js", StandardCharsets.UTF_8);
        ExecuteMessage execute = DockerSandBox.execute(LanguageCmdEnum.JAVASCRIPT, code);
        System.out.println(execute);
    }

    @Test
    void testTs() throws InterruptedException {
        String code = ResourceUtil.readStr("languageCode/main.ts", StandardCharsets.UTF_8);
        ExecuteMessage execute = DockerSandBox.execute(LanguageCmdEnum.TYPESCRIPT, code);
        System.out.println(execute);
    }

    @Test
    void testGo() throws InterruptedException {
        String code = ResourceUtil.readStr("languageCode/main.go", StandardCharsets.UTF_8);
        ExecuteMessage execute = DockerSandBox.execute(LanguageCmdEnum.GO, code);
        System.out.println(execute);
    }
}
