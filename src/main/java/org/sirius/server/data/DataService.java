package org.sirius.server.data;

import lombok.Data;
import org.sirius.server.cache.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gaoliandi
 * @time 2023/7/27
 */
@Service
@Scope("prototype")
@Data
public class DataService {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private CachingService cachingService;
    @AutoBean
    private long playerId;
    private final Map<Class<?>, Object> beanPool = new HashMap<>();
    private final Map<Class<?>, Object> dbPool = new HashMap<>();
    private final Map<Class<?>, List<?>> dbListPool = new HashMap<>();
    private long inactiveTimeStamp;

    public Object getBean(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        if (!beanPool.containsKey(clazz)) {
            Object base = cachingService.getConstructor(clazz).newInstance();
            configurableListableBeanFactory.autowireBean(base);
            beanPool.put(clazz, configurableListableBeanFactory.initializeBean(base, clazz.getName()));
            autowireBean(base);
        }
        return beanPool.get(clazz);
    }

    public Object[] getParameters(Method method) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        List<Object> paramList = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            Class<?> clazz = parameter.getType();
            if (!beanPool.containsKey(clazz)) {
                Object base = cachingService.getConstructor(clazz).newInstance();
                configurableListableBeanFactory.autowireBean(base);
                beanPool.put(clazz, configurableListableBeanFactory.initializeBean(base, clazz.getName()));
                autowireBean(base);
            }
            paramList.add(beanPool.get(clazz));
        }
        return paramList.toArray();
    }

    public void autowireBean(Object object) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (object == null) {
            return;
        }
        for (Field field : object.getClass().getDeclaredFields()) {
            Class<?> fieldClass = field.getType();
            field.setAccessible(true);
            if (fieldClass.getName().contains("CglibAopProxy")) {
                Object warpper = field.get(object);
                autowireBean(warpper);
            } else if (field.getAnnotation(AutoBean.class) != null) {
                if (!beanPool.containsKey(fieldClass)) {
                    Object base = cachingService.getConstructor(fieldClass).newInstance();
                    configurableListableBeanFactory.autowireBean(base);
                    beanPool.put(fieldClass, configurableListableBeanFactory.initializeBean(base, fieldClass.getName()));
                    autowireBean(base);
                }
                field.set(object, beanPool.get(fieldClass));
            } else if (field.getAnnotation(AutoDB.class) != null) {
                autowireDB(object, field);
            }
        }
    }

    public void autowireDB(Object object, Field field) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> entityClass = cachingService.getEntityClass(field);
        if (field.getType() == List.class) {
            if (dbListPool.containsKey(entityClass)) {
                field.set(object, dbListPool.get(entityClass));
            } else {
                JpaRepository repository = cachingService.getJpaRepository(entityClass);
                Object example = cachingService.getConstructor(entityClass).newInstance();
                cachingService.getIdField(entityClass).set(example, playerId);
                List<?> dataList = repository.findAll(Example.of(example));
                dbListPool.put(entityClass, dataList);
                field.set(object, dataList);
            }
        } else {
            if (dbPool.containsKey(entityClass)) {
                field.set(object, dbPool.get(entityClass));
            } else {
                JpaRepository repository = cachingService.getJpaRepository(entityClass);
                Object data = repository.findById(playerId).orElse(null);
                if (data != null) {
                    dbPool.put(entityClass, data);
                }
                field.set(object, data);
            }
        }
    }
}
