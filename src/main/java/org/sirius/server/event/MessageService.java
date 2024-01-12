package org.sirius.server.event;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;

/**
 * @author Gaoliandi
 * @Date 2024/1/12
 */
@Service
public class MessageService {
    @Autowired
    private ApplicationEventPublisher pushBuilder;
    @Autowired
    private JdkSerializationRedisSerializer serializer;
    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

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
