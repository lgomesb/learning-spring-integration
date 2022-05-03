package br.com.poc.spring.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.Setter;

@Setter
@ConfigurationProperties(prefix = "async.film-job")
@EnableAsync
@Configuration
public class AsyncFilmServiceConfig {
    /**
     * Number of core threads (default number of threads)
     */
    private int corePoolSize;
    /**
     * Maximum number of threads
     */
    private int maxPoolSize;
    /**
     * Allowed thread idle time (unit: default: seconds)
     */
    private int keepAliveSeconds;
    /**
     * Buffer queue size
     */
    private int queueCapacity;
    /**
     * Thread pool name prefix
     */
    private String threadNamePrefix;

    @Bean
    public ThreadPoolTaskExecutor asyncFilmService() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        // Processing strategy of thread pool for rejecting tasks
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // When the task is completed, it will be closed automatically. The default value is false
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        // The core thread times out and exits. The default value is false
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
