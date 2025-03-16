package xyz.kbws;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import xyz.kbws.executor.ContainerPoolExecutor;
import xyz.kbws.dao.DockerDao;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/11/2
 * @description:
 */
@Slf4j
@Component
public class CleanContainerListener implements ApplicationListener<ContextClosedEvent> {

    @Resource
    private DockerDao dockerDao;

    @Resource
    private ContainerPoolExecutor containerPoolExecutor;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 清理所有容器以及残余文件
        containerPoolExecutor
                .getContainerPool()
                .forEach(containerInfo -> {
                    FileUtil.del(containerInfo.getCodePathName());
                    dockerDao.cleanContainer(containerInfo.getContainerId());
                });
        log.info("container clean end...");
    }
}
