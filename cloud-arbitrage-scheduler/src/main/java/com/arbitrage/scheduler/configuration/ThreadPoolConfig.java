package com.arbitrage.scheduler.configuration;


import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static com.arbitrage.api.constants.CommonConstants.LOG_ID_KEY;

@Slf4j
@Configuration
public class ThreadPoolConfig {
    @Value("${spring.task.execution.pool.core-size:4}")
    private int corePoolSize;
    @Value("${spring.task.execution.pool.max-size:16}")
    private int maxPoolSize;
    @Value("${spring.task.execution.pool.queue-capacity:1000}")
    private int queueCapacity;
    @Value("${spring.task.execution.pool.keep-alive:10}")
    private int keepAliveSeconds;

    /**
     * 通用线程池
     */
    @Bean("commonThreadPool")
    public ThreadPoolTaskExecutor commonThreadPool() {
        log.info("核心线程数: {}, 最大线程数: {}, 队列容量: {}, 保活时长: {}s", corePoolSize, maxPoolSize, queueCapacity, keepAliveSeconds);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("commonThreadPool-");
        executor.setTaskDecorator(new MdcTaskDecorator());
        return executor;
    }

    public static class MdcTaskDecorator implements TaskDecorator {
        @Override
        public @NonNull Runnable decorate(@NonNull Runnable runnable) {
            String logId = MDC.get(LOG_ID_KEY);
            return () -> {
                try {
                    MDC.put(LOG_ID_KEY, logId);
                    runnable.run();
                } finally {
                    MDC.remove(LOG_ID_KEY);
                }
            };
        }
    }
}
