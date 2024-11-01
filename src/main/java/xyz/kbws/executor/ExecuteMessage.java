package xyz.kbws.executor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kbws
 * @date 2024/11/2
 * @description: 响应类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteMessage {
    private boolean success;
    private String messages;
    private String errorMessage;
}
