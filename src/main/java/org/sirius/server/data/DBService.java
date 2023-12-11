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
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class DBService {
    @Autowired
    private CachingService cachingService;
    @AutoBean
    private long playerId;
    private final Map<Class<?>, Object> dataPool = new HashMap<>();
    private final Map<Class<?>, Object> dataListPool = new HashMap<>();
    private final Map<Class<?>, Object> dataMapPool = new HashMap<>();

    public Object getAutoData(Field field) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> actualClass = cachingService.getAutoDBActualClass(field);
        Object warpper;
        if (field.getType() == List.class) {
            if (dataListPool.containsKey(actualClass)) {
                warpper = dataListPool.get(actualClass);
            } else {
                JpaRepository repository = cachingService.getJpaRepositoryName(actualClass);
                Object example = cachingService.getConstructor(actualClass).newInstance();
                cachingService.getIdField(actualClass).set(example, playerId);
                warpper = repository.findAll(Example.of(example));
                dataListPool.put(actualClass, warpper);
            }
        } else if (field.getType() == Map.class) {
            if (dataMapPool.containsKey(actualClass)) {
                warpper = dataMapPool.get(actualClass);
            } else {
                JpaRepository repository = cachingService.getJpaRepositoryName(actualClass);
                Object example = cachingService.getConstructor(actualClass).newInstance();
                cachingService.getIdField(actualClass).set(example, playerId);
                List<?> list = repository.findAll(Example.of(example));
                warpper = list.stream().collect(Collectors.groupingBy(e -> {
                    try {
                        return cachingService.getMapKeyMethod(actualClass).invoke(e);
                    } catch (IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException ex) {
                        throw new RuntimeException(ex);
                    }
                }));
                dataMapPool.put(actualClass, warpper);
            }
        } else {
            if (dataPool.containsKey(actualClass)) {
                warpper = dataPool.get(actualClass);
            } else {
                JpaRepository repository = cachingService.getJpaRepositoryName(actualClass);
                warpper = repository.findById(playerId).orElse(null);
                dataPool.put(actualClass, warpper);
            }
        }
        return warpper;
    }
}
