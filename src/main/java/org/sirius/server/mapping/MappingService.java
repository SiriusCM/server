package org.sirius.server.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author gaoliandi
 * @time 2023/8/17
 */
@Service
public class MappingService {
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    @Autowired
    private Map<String, RequestMappingInfo> mappingInfoMap;

    public void registerMapping(String name, Object service) {
        for (Method method : service.getClass().getDeclaredMethods()) {
            RequestMappingInfo mappingInfo = RequestMappingInfo.paths(name + '/' + method.getName()).methods(RequestMethod.POST).build();
            handlerMapping.registerMapping(mappingInfo, service, method);
            mappingInfoMap.put(method.getName(), mappingInfo);
        }
    }

    public void unRegisterMapping(String name, Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            handlerMapping.unregisterMapping(mappingInfoMap.get(name + '/' + method.getName()));
        }
    }
}
