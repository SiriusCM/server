package com.sirius.server;

import com.sirius.server.thread.DBThread;
import com.sirius.server.thread.LogicThread;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class Config implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Bean("roleId")
    public AtomicLong roleId(StringRedisTemplate stringRedisTemplate) {
        return new AtomicLong(1_0000_0000);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean("bossGroup")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(1);
    }

    @Bean("workerGroup")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    public List<LogicThread> getLogicThreadList() {
        List<LogicThread> logicThreadList = new ArrayList<>();
        int processors = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < processors; i++) {
            LogicThread logicThread = new LogicThread("logic-" + i);
            logicThread.start();
            logicThreadList.add(logicThread);
        }
        return logicThreadList;
    }

    @Bean
    public Map<EventLoop, DBThread> getDbThreadMap(@Qualifier("workerGroup") EventLoopGroup workerGroup) {
        Map<EventLoop, DBThread> dbThreadMap = new HashMap<>();
        for (EventExecutor eventExecutor : workerGroup) {
            EventLoop eventLoop = (EventLoop) eventExecutor;
            DBThread dbThread = new DBThread("db-" + eventLoop.hashCode());
            dbThread.start();
            dbThreadMap.put(eventLoop, dbThread);
        }
        return dbThreadMap;
    }

    @Bean
    public List<Class<?>> getClassList() throws IOException, ClassNotFoundException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
        List<Class<?>> classList = new ArrayList<>();
        for (Resource resource : resolver.getResources("classpath:**/*.class")) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
            String className = metadataReader.getClassMetadata().getClassName();
            Class<?> clazz = Class.forName(className);
            classList.add(clazz);
        }
        return classList;
    }
}
