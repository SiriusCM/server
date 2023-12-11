package org.sirius.server;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author 高连棣
 * @date 2022/10/12
 **/
@Configuration
public class Configurer implements AsyncConfigurer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Executor getAsyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public ThreadFactory threadFactory() {
        return Thread.ofVirtual().factory();
    }

    @Bean("boss")
    public NioEventLoopGroup nioEventLoopGroupBoss() {
        return new NioEventLoopGroup(2, new DefaultThreadFactory("boss"));
    }

    @Bean("worker")
    public NioEventLoopGroup nioEventLoopGroupWorker(ThreadFactory threadFactory) {
        return new NioEventLoopGroup(10000, threadFactory);
    }

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean("oa-date")
    public SimpleDateFormat simpleDateFormatOA() {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    }

    @Bean("task-date")
    public SimpleDateFormat simpleDateFormatTask() {
        return new SimpleDateFormat("yyyy/MM/dd/");
    }
}