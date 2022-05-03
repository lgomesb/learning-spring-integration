package br.com.poc.multthread.config;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


/**
 * @author Leandro Barbosa
 * @see https://dzone.com/articles/multi-threading-in-spring-boot-using-completablefu
 */

@Configuration
@EnableAsync
public class AsyncConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Bean (name = "taskExecutor")
    public Executor taskExecutor() {
        LOGGER.info("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("PocThread-");
        executor.initialize();
        return executor;
    }
	
}
