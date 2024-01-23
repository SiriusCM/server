package org.sirius.server.event;

import lombok.SneakyThrows;
import org.sirius.server.bean.BeanService;
import org.sirius.server.cache.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Gaoliandi
 * @Date 2024/1/12
 */
@Service
public class DispatchService {
    @Autowired
    private ApplicationEventPublisher pushBuilder;
    @Autowired
    private JdkSerializationRedisSerializer serializer;
    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;
    @Autowired
    private CachingService cachingService;

    public void dispatchMsg(BeanService beanService, int msgId, byte[] data) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        //CodedInputStream codedInputStream = CodedInputStream.newInstance(data, 4, data.length - 4);
        beanService.getBeanPool().put(int.class, msgId);
        beanService.getBeanPool().put(byte[].class, data);
        for (Method method : cachingService.getDispatchMethods(msgId)) {
            method.invoke(beanService.getBean(method.getDeclaringClass()), beanService.getParameters(method));
        }
    }

    @EventListener({ApplicationReadyEvent.class})
    public void onReady(ApplicationReadyEvent event) {
        lettuceConnectionFactory.getConnection().subscribe(this::onMessage, "event".getBytes());
    }

    @SneakyThrows
    public void onMessage(Message message, byte[] pattern) {
        byte[] channel = message.getChannel();
        byte[] body = message.getBody();
        Object event = serializer.deserialize(message.getBody());
        if (event == null) {
            return;
        }
        pushBuilder.publishEvent(event);
    }
}
