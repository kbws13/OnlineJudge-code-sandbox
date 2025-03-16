package xyz.kbws.entity;

import lombok.Data;

/**
 * @author kbws
 * @date 2024/11/2
 * @description: 请求类
 */
@Data
public class ExecuteRequest {
    private String language;
    private String code;
}
