package xyz.kbws;

import cn.hutool.core.io.resource.ResourceUtil;
import org.junit.jupiter.api.Test;
import xyz.kbws.entity.ExecuteMessage;
import xyz.kbws.enums.LanguageCmdEnum;

import java.nio.charset.StandardCharsets;

/**
 * @author kbws
 * @date 2024/11/2
 * @description:
 */
public class DockerSandBoxTest {

    @Test
    void testJava() throws InterruptedException {
        DockerSandBox dockerSandBox = new DockerSandBox();
        String code = ResourceUtil.readStr("languageCode/Main.java", StandardCharsets.UTF_8);
        ExecuteMessage execute = dockerSandBox.execute(LanguageCmdEnum.JAVA, code);
        Thread.sleep(10000);
        System.out.println(execute);
    }

    @Test
    void testCpp() throws InterruptedException {
        DockerSandBox dockerSandBox = new DockerSandBox();
        String code = ResourceUtil.readStr("languageCode/main.cpp", StandardCharsets.UTF_8);
        ExecuteMessage execute = dockerSandBox.execute(LanguageCmdEnum.CPP, code);
        System.out.println(execute);
    }

    @Test
    void testC() throws InterruptedException {
        DockerSandBox dockerSandBox = new DockerSandBox();
        String code = ResourceUtil.readStr("languageCode/main.c", StandardCharsets.UTF_8);
        ExecuteMessage execute = dockerSandBox.execute(LanguageCmdEnum.C, code);
        System.out.println(execute);
    }

    @Test
    void testPython() throws InterruptedException {
        DockerSandBox dockerSandBox = new DockerSandBox();
        String code = ResourceUtil.readStr("languageCode/main.py", StandardCharsets.UTF_8);
        ExecuteMessage execute = dockerSandBox.execute(LanguageCmdEnum.PYTHON3, code);
        System.out.println(execute);
    }

    @Test
    void testJs() throws InterruptedException {
        DockerSandBox dockerSandBox = new DockerSandBox();
        String code = ResourceUtil.readStr("languageCode/main.js", StandardCharsets.UTF_8);
        ExecuteMessage execute = dockerSandBox.execute(LanguageCmdEnum.JAVASCRIPT, code);
        System.out.println(execute);
    }

    @Test
    void testTs() throws InterruptedException {
        DockerSandBox dockerSandBox = new DockerSandBox();
        String code = ResourceUtil.readStr("languageCode/main.ts", StandardCharsets.UTF_8);
        ExecuteMessage execute = dockerSandBox.execute(LanguageCmdEnum.TYPESCRIPT, code);
        System.out.println(execute);
    }

    @Test
    void testGo() throws InterruptedException {
        DockerSandBox dockerSandBox = new DockerSandBox();
        String code = ResourceUtil.readStr("languageCode/main.go", StandardCharsets.UTF_8);
        ExecuteMessage execute = dockerSandBox.execute(LanguageCmdEnum.GO, code);
        System.out.println(execute);
    }
}
