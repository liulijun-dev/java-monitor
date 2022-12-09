package org.github.java.monitor.bootstrap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.github.java.monitor.util.LogUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * description
 *
 * @author LiJun.Liu
 * @since 2022/12/8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExecutorManager {
    private static final Set<ExecutorService> executors = new HashSet<>();

    public static void addExecutorService(ExecutorService executor) {
        executors.add(executor);
    }

    public static void stopAll(long timeout, TimeUnit unit) {
        for (ExecutorService executorService : executors) {
            try {
                executorService.shutdown();
                executorService.awaitTermination(timeout, unit);
            } catch (InterruptedException e) {
                LogUtil.logMethodError("ExecutorManager.stopAll", e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
