package org.sirius.server.cache;


import jakarta.persistence.Id;
import org.sirius.server.dispatch.MsgId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gaoliandi
 * @time 2023/7/27
 */
@Service
public class CachingService {
    @Autowired
    private Map<String, JpaRepository<?, ?>> jpaRepositoryMap;

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
    public Field getIdField(Class<?> actualClass) {
        for (Field field : actualClass.getDeclaredFields()) {
            if (field.getAnnotation(Id.class) != null) {
                field.setAccessible(true);
                return field;
            }
        }
        return null;
    }

    @Cacheable(cacheNames = "mapKeyMethod")
    public Method getMapKeyMethod(Class<?> actualClass) throws NoSuchMethodException {
        Field field = getIdField(actualClass);
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        String methodName = field.getName();
        methodName = "get" + methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        Method m = actualClass.getMethod(methodName);
        m.setAccessible(true);
        return m;
    }

    @Cacheable(cacheNames = "jpaRepository")
    public JpaRepository<?, ?> getJpaRepositoryName(Class<?> actualClass) throws IOException, ClassNotFoundException {
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
    public Class<?> getAutoDBActualClass(Object object) throws IOException, ClassNotFoundException {
        if (object instanceof Field) {
            Field field = (Field) object;
            field.setAccessible(true);
            Class<?> actualClass = getActualClass(field.getType(), field.getGenericType());
            if (getJpaRepositoryName(actualClass) != null) {
                return actualClass;
            }
        } else if (object instanceof Parameter) {
            Parameter parameter = (Parameter) object;
            Class<?> actualClass = getActualClass(parameter.getType(), parameter.getParameterizedType());
            if (getJpaRepositoryName(actualClass) != null) {
                return actualClass;
            }
        }
        return null;
    }

    private Class<?> getActualClass(Class<?> objectClass, Type type) {
        if (objectClass == List.class) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            objectClass = (Class<?>) types[0];
        } else if (objectClass == Map.class) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            objectClass = (Class<?>) types[1];
        }
        return objectClass;
    }
}
