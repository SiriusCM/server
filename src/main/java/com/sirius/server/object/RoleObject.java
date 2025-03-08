package com.sirius.server.object;

import com.sirius.server.IRoleBean;
import com.sirius.server.aop.MsgId;
import com.sirius.server.cache.CacheService;
import com.sirius.server.ioc.AutoBean;
import com.sirius.server.ioc.AutoCache;
import com.sirius.server.msg.Msg;
import io.netty.channel.ChannelHandlerContext;
import jakarta.websocket.Session;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Scope("prototype")
@Component
public class RoleObject extends WorldObject {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private List<IRoleBean> roleBeanList;

    private Map<Class<?>, IRoleBean> roleBeanMap;

    private final Map<Integer, List<Consumer<Msg.Message>>> msgIdConsumerMap = new HashMap<>();

    private final Map<Class<?>, List<Consumer<EventObject>>> eventConsumerMap = new HashMap<>();

    private final Map<Class<?>, Object> dbMap = new HashMap<>();

    private final Map<Class<?>, List<Object>> dbListMap = new HashMap<>();

    private ChannelHandlerContext channelHandlerContext;

    private Session session;

    private long logoutTime;

    @Override
    public void pulse() {
    }

    public void init() {
        setId(1001);
        long roleId = getId();
        roleBeanMap = roleBeanList.stream().collect(Collectors.toMap(IRoleBean::getClass, roleController -> roleController));
        roleBeanMap.forEach((aClass, roleBean) -> {
            for (Field field : aClass.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(AutoBean.class)) {
                        Object bean = roleBeanMap.get(field.getType());
                        field.set(roleBean, bean);
                    } else if (field.isAnnotationPresent(AutoCache.class)) {
                        Class<?> actualType = cacheService.getActualType(field);
                        JpaRepository<?, ?> jpaRepository = cacheService.getJpaRepository(actualType);
                        Object entity = actualType.getConstructor().newInstance();
                        Field f = actualType.getDeclaredField("roleId");
                        f.setAccessible(true);
                        f.set(entity, roleId);
                        Example example = Example.of(entity);
                        if (field.getType() == List.class) {
                            if (!dbListMap.containsKey(actualType)) {
                                List list = jpaRepository.findAll(example);
                                dbListMap.put(actualType, list);
                            }
                            field.set(roleBean, dbListMap.get(actualType));
                        } else {
                            if (!dbMap.containsKey(actualType)) {

                                Optional<?> db = jpaRepository.findOne(example);
                                dbMap.put(actualType, db.orElseGet(null));
                            }
                            field.set(roleBean, dbMap.get(actualType));
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            for (Method method : aClass.getDeclaredMethods()) {
                try {
                    method.setAccessible(true);
                    if (method.isAnnotationPresent(MsgId.class)) {
                        MsgId msgId = method.getAnnotation(MsgId.class);
                        msgIdConsumerMap.compute(msgId.id().getNumber(), (id, list) -> {
                            if (list == null) {
                                list = new ArrayList<>();
                            }
                            list.add(message -> {
                                try {
                                    method.invoke(roleBean, message);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            return list;
                        });

                    } else if (method.isAnnotationPresent(EventListener.class)) {
                        EventListener eventListener = method.getAnnotation(EventListener.class);
                        for (Class<?> eventClass : eventListener.classes()) {
                            eventConsumerMap.compute(eventClass, (clazz, list) -> {
                                if (list == null) {
                                    list = new ArrayList<>();
                                }
                                list.add(eventObject -> {
                                    try {
                                        method.invoke(roleBean, eventObject);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                                return list;
                            });
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        roleBeanList.forEach(IRoleBean::init);
    }

    public void dispatchMsg(Msg.Message message) {
        msgIdConsumerMap.get(message.getMsgIdCase().getNumber()).forEach(consumer -> consumer.accept(message));
    }

    public void publish(EventObject event) {
        eventConsumerMap.get(event.getClass()).forEach(consumer -> consumer.accept(event));
    }
}
