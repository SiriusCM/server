package com.sirius.server;

import io.netty.channel.EventLoop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class Config implements AsyncConfigurer {

    public static final Map<EventLoop, MainThread> eventLoopMainThreadMap = new HashMap<>();

    @Override
    public Executor getAsyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
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

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
