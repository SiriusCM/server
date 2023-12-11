package org.sirius.server.event;

import lombok.SneakyThrows;
import org.sirius.server.bean.AutoBean;
import org.sirius.server.bean.BeanService;
import org.sirius.server.cache.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author gaoliandi
 * @time 2023/6/29
 */
@Service
@Scope("prototype")
public class EventService {
    @Autowired
    private CachingService cachingService;
    @AutoBean
    private BeanService beanService;

    @SneakyThrows
    public void publishEvent(ApplicationEvent event) {
        Map<Class<?>, Object> servicePool = beanService.getBeanPool();
        for (Method method : cachingService.getEventMethods(event.getClass())) {
            method.invoke(servicePool.get(method.getDeclaringClass()), event);
        }
    }
}
