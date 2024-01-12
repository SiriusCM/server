package org.sirius.server;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
    @Value("${rmi.port}")
    private int port;

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
    public Registry registry() throws RemoteException {
        return LocateRegistry.createRegistry(port);
    }

    @Bean
    public JdkSerializationRedisSerializer jdkSerializationRedisSerializer() {
        return new JdkSerializationRedisSerializer();
    }

    @Bean
    public Random random() {
        return new Random();
    }
}