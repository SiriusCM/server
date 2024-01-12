package org.sirius.server.event;

import lombok.SneakyThrows;
import org.sirius.server.bean.AutoBean;
import org.sirius.server.bean.BeanService;
import org.sirius.server.cache.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CachingService cachingService;
    @AutoBean
    private BeanService beanService;
    @AutoBean
    private long playerId;

    @SneakyThrows
    public void publishLocal(ApplicationEvent event) {
        Map<Class<?>, Object> servicePool = beanService.getBeanPool();
        for (Method method : cachingService.getEventMethods(event.getClass())) {
            method.invoke(servicePool.get(method.getDeclaringClass()), event);
        }
    }

    public void publishRemote(ApplicationEvent event) {
        publishLocal(event);
        RemoteEvent remoteEvent = new RemoteEvent();
        remoteEvent.setPlayerId(playerId);
        remoteEvent.setEvent(event);
        stringRedisTemplate.convertAndSend("event", remoteEvent);
    }

    @EventListener(RemoteEvent.class)
    public void listenRemoteEvent(RemoteEvent event) {
        if (event.getPlayerId() != playerId) {
            return;
        }
        publishLocal(event.getEvent());
    }
}
