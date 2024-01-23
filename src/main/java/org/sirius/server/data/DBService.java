package org.sirius.server.data;


import org.sirius.server.bean.AutoBean;
import org.sirius.server.cache.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope("prototype")
public class DBService {
    @Autowired
    private CachingService cachingService;
    @AutoBean
    private long playerId;
    private final Map<Class<?>, Object> dataPool = new HashMap<>();
    private final Map<Class<?>, List<?>> dataListPool = new HashMap<>();

    public Object getAutoData(Field field) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> entityClass = cachingService.getEntityClass(field);
        if (field.getType() == List.class) {
            if (dataListPool.containsKey(entityClass)) {
                return dataListPool.get(entityClass);
            } else {
                JpaRepository repository = cachingService.getJpaRepository(entityClass);
                Object example = cachingService.getConstructor(entityClass).newInstance();
                cachingService.getIdField(entityClass).set(example, playerId);
                List<?> dataList = repository.findAll(Example.of(example));
                dataListPool.put(entityClass, dataList);
                return dataList;
            }
        } else {
            if (dataPool.containsKey(entityClass)) {
                return dataPool.get(entityClass);
            } else {
                JpaRepository repository = cachingService.getJpaRepository(entityClass);
                Object data = repository.findById(playerId).orElse(null);
                if (data != null) {
                    dataPool.put(entityClass, data);
                }
                return data;
            }
        }
    }
}
