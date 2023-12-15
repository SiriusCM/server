package org.sirius.server.bean;

import lombok.Getter;
import org.sirius.server.cache.CachingService;
import org.sirius.server.data.AutoDB;
import org.sirius.server.data.DBService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author gaoliandi
 * @time 2023/7/27
 */
@Service
@Scope("prototype")
@Getter
public class BeanService implements DisposableBean {
    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    private CachingService cachingService;
    @AutoBean
    private DBService dbService;
    private final Map<Class<?>, Object> beanPool = new HashMap<>();

    public Object getBean(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException {
        if (!beanPool.containsKey(clazz)) {
            Object base = getBeanBase(clazz);
            autowireBean(base);
        }
        return beanPool.get(clazz);
    }

    public Object[] getParameters(Method method) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IOException, ClassNotFoundException {
        List<Object> paramList = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            Class<?> clazz = parameter.getType();
            if (!beanPool.containsKey(clazz)) {
                Object base = getBeanBase(clazz);
                autowireBean(base);
            }
            paramList.add(beanPool.get(clazz));
        }
        return paramList.toArray();
    }

    public void autowireBean(Object object) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        if (object == null) {
            return;
        }
        for (Field field : object.getClass().getDeclaredFields()) {
            Class<?> fieldClass = field.getType();
            field.setAccessible(true);
            Object warpper = field.get(object);
            if (fieldClass.getName().contains("CglibAopProxy")) {
                autowireBean(warpper);
            } else if (field.getAnnotation(AutoBean.class) != null) {
                if (!beanPool.containsKey(fieldClass)) {
                    Object base = getBeanBase(fieldClass);
                    autowireBean(base);
                }
                field.set(object, beanPool.get(fieldClass));
            } else if (field.getAnnotation(AutoDB.class) != null) {
                warpper = dbService.getAutoData(field);
                field.set(object, warpper);
            }
        }
    }

    private Object getBeanBase(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object base = cachingService.getConstructor(clazz).newInstance();
        configurableListableBeanFactory.autowireBean(base);
        beanPool.put(clazz, configurableListableBeanFactory.initializeBean(base, clazz.getName()));
        return base;
    }

    @Override
    public void destroy() throws Exception {
        beanPool.values().stream().filter(Objects::nonNull).forEach(configurableListableBeanFactory::destroyBean);
    }
}
