package org.sirius.server.cache;


import jakarta.persistence.Id;
import lombok.Data;
import org.sirius.server.bean.BeanService;
import org.sirius.server.dispatch.MsgId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaoliandi
 * @time 2023/7/27
 */
@Service
@Data
public class CachingService {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private Map<String, JpaRepository<?, ?>> jpaRepositoryMap;

    private final Map<Long, BeanService> beanServiceMap = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 */10 * * * *")
    public void clearBean() {
        long now = System.currentTimeMillis();
        Iterator<BeanService> iterator = beanServiceMap.values().iterator();
        while (iterator.hasNext()) {
            BeanService beanService = iterator.next();
            if (beanService.getInactiveTimeStamp() - now > 10 * 60 * 1000) {
                beanService.getBeanPool().values().stream().filter(Objects::nonNull).forEach(configurableListableBeanFactory::destroyBean);
            }
        }
    }

    @Cacheable(cacheNames = "dispatch")
    public List<Method> getDispatchMethods(int id) throws IOException, ClassNotFoundException {
        List<Method> methodList = new ArrayList<>();
        for (Class<?> clazz : getClassPool()) {
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
    public List<Method> getEventMethods(Class<?> eventClass) throws IOException, ClassNotFoundException {
        List<Method> methodList = new ArrayList<>();
        for (Class<?> clazz : getClassPool()) {
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

    @Cacheable(cacheNames = "constructor")
    public Constructor<?> getConstructor(Class<?> constructorClass) throws NoSuchMethodException {
        return constructorClass.getConstructor();
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

    @Cacheable(cacheNames = "jpaRepository")
    public JpaRepository<?, ?> getJpaRepository(Class<?> actualClass) throws IOException, ClassNotFoundException {
        for (Class<?> clazz : getClassPool()) {
            if (JpaRepository.class.isAssignableFrom(clazz)) {
                ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericInterfaces()[0];
                Class<?> oc = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                if (actualClass == oc) {
                    String jpaRepositoryName = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);
                    return jpaRepositoryMap.get(jpaRepositoryName);
                }
            }
        }
        return null;
    }

    @Cacheable(cacheNames = "autoDB")
    public Class<?> getEntityClass(Field field) {
        if (field.getType() == List.class) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] types = parameterizedType.getActualTypeArguments();
            return (Class<?>) types[0];
        } else {
            return field.getType();
        }
    }

    @Cacheable(cacheNames = "class")
    public List<Class<?>> getClassPool() throws IOException, ClassNotFoundException {
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
}
