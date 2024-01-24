package org.sirius.server.cache;


import jakarta.persistence.Id;
import org.sirius.server.data.DataService;
import org.sirius.server.dispatch.MsgId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author gaoliandi
 * @time 2023/7/27
 */
@Service
public class CachingService {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private List<Class<?>> classList;
    @Autowired
    private Map<String, JpaRepository<?, ?>> jpaRepositoryMap;
    @Autowired
    private Map<Long, DataService> dataServiceMap;

    @Scheduled(cron = "* * * * * *")
    public void clearBean() {
        long now = System.currentTimeMillis();
        Iterator<DataService> iterator = dataServiceMap.values().iterator();
        while (iterator.hasNext()) {
            DataService dataService = iterator.next();
            if (dataService.getInactiveTimeStamp() - now > 10 * 60 * 1000) {
                dataService.getBeanPool().values().stream().filter(Objects::nonNull).forEach(configurableListableBeanFactory::destroyBean);
            }
        }
    }

    @Cacheable(cacheNames = "constructor")
    public Constructor<?> getConstructor(Class<?> constructorClass) throws NoSuchMethodException {
        return constructorClass.getConstructor();
    }

    @Cacheable(cacheNames = "dispatch")
    public List<Method> getDispatchMethods(int id) {
        List<Method> methodList = new ArrayList<>();
        for (Class<?> clazz : classList) {
            for (Method method : clazz.getDeclaredMethods()) {
                method.setAccessible(true);
                MsgId msgId = method.getAnnotation(MsgId.class);
                if (msgId != null) {
                    for (int i : msgId.id()) {
                        if (id == i) {
                            methodList.add(method);
                        }
                    }
                }
            }
        }
        return methodList;
    }

    @Cacheable(cacheNames = "event")
    public List<Method> getEventMethods(Class<?> eventClass) {
        List<Method> methodList = new ArrayList<>();
        for (Class<?> clazz : classList) {
            for (Method method : clazz.getDeclaredMethods()) {
                method.setAccessible(true);
                EventListener listener = method.getAnnotation(EventListener.class);
                if (listener != null) {
                    for (Class<?> aClass : listener.value()) {
                        if (aClass == eventClass) {
                            methodList.add(method);
                        }
                    }
                }
            }
        }
        return methodList;
    }

    @Cacheable(cacheNames = "entity")
    public Class<?> getEntityClass(Field field) {
        if (field.getType() == List.class) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] types = parameterizedType.getActualTypeArguments();
            return (Class<?>) types[0];
        } else {
            return field.getType();
        }
    }

    @Cacheable(cacheNames = "jpaRepository")
    public JpaRepository<?, ?> getJpaRepository(Class<?> entityClass) {
        for (Class<?> clazz : classList) {
            if (JpaRepository.class.isAssignableFrom(clazz)) {
                ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericInterfaces()[0];
                Class<?> oc = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                if (entityClass == oc) {
                    String jpaRepositoryName = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
                    return jpaRepositoryMap.get(jpaRepositoryName);
                }
            }
        }
        return null;
    }

    @Cacheable(cacheNames = "idField")
    public Field getIdField(Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.getAnnotation(Id.class) != null) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }
}
