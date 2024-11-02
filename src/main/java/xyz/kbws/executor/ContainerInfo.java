package xyz.kbws.executor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kbws
 * @date 2024/11/2
 * @description: 容器相关信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContainerInfo {

    /**
     * 容器ID
     */
    private String containerId;

    /**
     * 宿主机临时代码存储文件，目的是在容器初始化时就创建好临时目录，在最后清理的时候删除临时文件
     */
    private String codePathName;

    /**
     * 记录容器的上次活跃时间
     */
    private long lastActivityTime;

    /**
     * 错误计数 默认为 0
     */
    private int errorCount = 0;
}
