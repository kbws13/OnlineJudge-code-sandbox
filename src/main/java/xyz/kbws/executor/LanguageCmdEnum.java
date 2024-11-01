package xyz.kbws.executor;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author kbws
 * @date 2024/11/2
 * @description: 编程语言枚举
 * 不需要编译的语言编译的 cmd 设置为空
 */
@Getter
public enum LanguageCmdEnum {
    JAVA("java", "Main.java", new String[]{"javac", "-encoding", "utf-8", "Main.java"}, new String[]{"java", "-Dfile.encoding=UTF-8", "Main"}),
    CPP("cpp", "main.cpp", new String[]{"g++", "-finput-charset=UTF-8", "-fexec-charset=UTF-8", "-o", "main", "main.cpp"}, new String[]{"./main"}),
    C("c", "main.c", new String[]{"gcc", "-finput-charset=UTF-8", "-fexec-charset=UTF-8", "-o", "main", "main.c"}, new String[]{"./main"}),
    PYTHON3("python", "main.py", null, new String[]{"python3", "main.py"}),
    JAVASCRIPT("javascript", "main.js", null, new String[]{"node", "main.js"}),
    TYPESCRIPT("typescript", "main.ts", null, new String[]{"node", "main.ts"}),
    GO("go", "main.go", null, new String[]{"go", "run", "main.go"}),
    ;

    private final String language;

    /**
     * 保存的文件名
     */
    private final String saveFileName;
    private final String[] compileCmd;
    private final String[] runCmd;

    LanguageCmdEnum(String language, String saveFileName, String[] compileCmd, String[] runCmd) {
        this.language = language;
        this.saveFileName = saveFileName;
        this.compileCmd = compileCmd;
        this.runCmd = runCmd;
    }

    /**
     * 根据 language 获取枚举
     */
    public static LanguageCmdEnum getEnumByValue(String language) {
        if (StringUtils.isBlank(language)) {
            return null;
        }
        for (LanguageCmdEnum languageCmdEnum : LanguageCmdEnum.values()) {
            if (languageCmdEnum.language.equals(language)) {
                return languageCmdEnum;
            }
        }
        return null;
    }
}
