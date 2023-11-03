package xyz.kbws.ojcodesandbox.model;

import lombok.Data;

/**
 * @author kbws
 * @date 2023/11/3
 * @description: 进程执行信息
 */
@Data
public class ExecuteMessage {

    private Integer exitValue;

    private String message;

    private String errorMessage;

    private Long time;
}
