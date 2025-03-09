package com.sirius.server.object;

import com.sirius.server.thread.DBThread;
import com.sirius.server.ioc.IRoleBean;
import com.sirius.server.aop.MsgId;
import com.sirius.server.cache.CacheService;
import com.sirius.server.global.GlobalService;
import com.sirius.server.ioc.AutoBean;
import com.sirius.server.ioc.AutoCache;
import com.sirius.server.msg.Msg;
import com.sirius.server.thread.ThreadService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
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

@EqualsAndHashCode(callSuper = true)
@Data
@Scope("prototype")
@Component
public class RoleObject extends WorldObject {
    @Autowired
    private GlobalService globalService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private List<IRoleBean> roleBeanList;

    private RoleState roleState = RoleState.LOGIN;

    private Queue<Consumer<DBThread>> dbQueue = new LinkedList<>();

    private final Map<Class<?>, Object> poolMap = new HashMap<>();

    private final Map<Class<?>, List<Object>> poolListMap = new HashMap<>();

    private final Map<Integer, List<Consumer<Msg.Message>>> msgIdConsumerMap = new HashMap<>();

    private final Map<Class<?>, List<Consumer<EventObject>>> eventConsumerMap = new HashMap<>();

    private ChannelHandlerContext channelHandlerContext;

    private Session session;

    private long logoutTime;

    public RoleObject() {
        setId(1001);
        poolMap.put(RoleObject.class, this);
        autowire();
    }

    @MsgId(id = Msg.Message.MsgIdCase.LOGIN_REQUEST)
    public void loginRequest(Msg.LoginRequest loginRequest) {
        globalService.getLoginQueue().add(this);
    }

    @Override
    public void pulse() {
    }

    public void loginFinish() {
        EventLoop eventLoop = channelHandlerContext.channel().eventLoop();
        threadService.getDbThreadMap().get(eventLoop).addDBQueue(this);
        roleBeanList.forEach(roleBean -> poolMap.put(roleBean.getClass(), roleBean));
        autowire();
        roleBeanList.forEach(IRoleBean::init);
        this.roleState = RoleState.ONLINE;
    }

    public void dispatchMsg(Msg.Message message) {
        msgIdConsumerMap.get(message.getMsgIdCase().getNumber()).forEach(consumer -> consumer.accept(message));
    }

    public void publish(EventObject event) {
        eventConsumerMap.get(event.getClass()).forEach(consumer -> consumer.accept(event));
    }

    public void addDBQueue(Consumer<DBThread> transaction) {
        dbQueue.add(transaction);
    }

    public void autowire() {
        long roleId = getId();
        poolMap.forEach((aClass, roleBean) -> {
            for (Field field : aClass.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(AutoBean.class)) {
                        Object bean = poolMap.get(field.getType());
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
                            if (!poolListMap.containsKey(actualType)) {
                                List list = jpaRepository.findAll(example);
                                poolListMap.put(actualType, list);
                            }
                            field.set(roleBean, poolListMap.get(actualType));
                        } else {
                            if (!poolMap.containsKey(actualType)) {
                                Optional<?> db = jpaRepository.findOne(example);
                                poolMap.put(actualType, db.orElseGet(null));
                            }
                            field.set(roleBean, poolMap.get(actualType));
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
    }
}
