package org.sirius.server;

import org.sirius.server.data.DataService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.rmi.Remote;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MapConfigurer {
    @Bean
    public Map<String, Remote> remoteMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, RequestMappingInfo> mappingInfoMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<Long, DataService> dataServiceMap() {
        return new ConcurrentHashMap<>();
    }
}
