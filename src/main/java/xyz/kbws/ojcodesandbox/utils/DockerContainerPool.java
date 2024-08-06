package xyz.kbws.ojcodesandbox.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kbws
 * @date 2024/7/28
 * @description: Docker 容器池
 */
@Component
public class DockerContainerPool {
    private final int initialPoolSize;
    private final int maxPoolSize;
    private final String imageName;
    private final Queue<String> containerPool;
    private final Set<String> busyContainers;
    private final Object lock = new Object();
    private final DockerClient dockerClient;

    public DockerContainerPool(@Value("${docker.pool.initial-size}") int initialPoolSize,
                               @Value("${docker.pool.max-size}") int maxPoolSize,
                               @Value("${docker.image.name}") String imageName,
                               DockerClient dockerClient) {
        this.initialPoolSize = initialPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.imageName = imageName;
        this.dockerClient = dockerClient;
        this.containerPool = new LinkedList<>();
        this.busyContainers = ConcurrentHashMap.newKeySet();
        initializeContainers(initialPoolSize);
    }

    private void initializeContainers(int numberOfContainers) {
        for (int i = 0; i < numberOfContainers; i++) {
            createAndAddContainer(i);
        }
    }

    private void createAndAddContainer(int index) {
        try {
            dockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback()).awaitCompletion();

            HostConfig hostConfig = new HostConfig()
                    .withMemory(100 * 1000 * 1000L)
                    .withMemorySwap(1000L)
                    .withCpuCount(1L)
                    .withSecurityOpts(Arrays.asList("seccomp=" + ResourceUtil.readUtf8Str("profile.json")))
                    .withBinds(new Bind("/tmp", new Volume("/app")));

            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                    .withHostConfig(hostConfig)
                    .withNetworkDisabled(true)
                    .withReadonlyRootfs(true)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .withTty(true)
                    .exec();

            dockerClient.startContainerCmd(container.getId()).exec();
            containerPool.add(container.getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getContainer() throws InterruptedException {
        synchronized (lock) {
            while (containerPool.isEmpty() && busyContainers.size() < maxPoolSize) {
                createAndAddContainer(busyContainers.size() + containerPool.size());
            }
            while (containerPool.isEmpty()) {
                lock.wait();
            }
            String containerId = containerPool.poll();
            busyContainers.add(containerId);
            return containerId;
        }
    }

    public void releaseContainer(String containerId) {
        synchronized (lock) {
            busyContainers.remove(containerId);
            containerPool.add(containerId);
            lock.notifyAll();
        }
    }

    public void shutdown() {
        for (String containerId : containerPool) {
            stopAndRemoveContainer(containerId);
        }
    }

    private void stopAndRemoveContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            dockerClient.removeContainerCmd(containerId).exec();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DockerClient getDockerClient() {
        return dockerClient;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }
}
