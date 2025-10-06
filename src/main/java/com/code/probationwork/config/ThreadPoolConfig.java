package com.code.probationwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync  // 启用Spring异步处理功能
public class ThreadPoolConfig {
    @Bean("streamExecutor")
    public Executor streamExecutor() {
        // 创建线程池任务执行器
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：保持活跃的最小线程数
        executor.setCorePoolSize(2);
        // 最大线程数：线程池能创建的最大线程数
        executor.setMaxPoolSize(5);
        // 队列容量：当核心线程都忙时，新任务进入队列等待
        executor.setQueueCapacity(50);
        // 线程空闲时间：非核心线程的空闲存活时间（秒）
        executor.setKeepAliveSeconds(60);
        // 拒绝策略：当线程池和队列都满时的处理策略
        // CallerRunsPolicy：由调用线程执行任务（避免任务丢失）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间：关闭时等待任务完成的最大时间（秒）
        executor.setAwaitTerminationSeconds(60);
        // 初始化线程池
        executor.initialize();
        return executor;
    }

    @Bean("emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：邮件发送需要较多线程处理并发
        executor.setCorePoolSize(3);
        // 最大线程数：支持大量邮件并发发送
        executor.setMaxPoolSize(10);
        // 队列容量：邮件发送允许较多任务排队
        executor.setQueueCapacity(200);
        // 线程空闲时间：邮件发送线程可以保持较长时间活跃
        executor.setKeepAliveSeconds(120);
        // 线程名前缀：便于识别邮件发送线程
        executor.setThreadNamePrefix("Email-");
        // 拒绝策略：邮件发送失败时由调用线程执行（避免邮件丢失）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间：邮件发送需要更多时间完成
        executor.setAwaitTerminationSeconds(60);
        // 初始化线程池
        executor.initialize();
        return executor;
    }
    @Bean("messageExecutor")
    public Executor messageExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("Message-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}