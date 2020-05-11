package com.color.FileProcessing.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableAsync
public class ExecutorConfig {

    @Bean
    public Executor taskExecutor() {

    log.info("start asyncServiceExecutor");

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    //配置核心线程数
    executor.setCorePoolSize(5);

    //配置最大线程数
    executor.setMaxPoolSize(5);

    //配置队列大小
    executor.setQueueCapacity(99999);

    //配置线程池中的线程的名称前缀
    executor.setThreadNamePrefix("async-service-");

    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    //执行初始化
    executor.initialize();

    return executor;

    }

}
