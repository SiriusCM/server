package com.sirius.server.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class CacheService {
    @Autowired
    private List<Class<?>> classList;
    @Autowired
    private List<JpaRepository<?, Long>> jpaRepositoryList;

    //    @Cacheable(cacheNames = "actualType")
    public Class<?> getActualType(Field field) {
        if (field.getType() == List.class) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] types = parameterizedType.getActualTypeArguments();
            return (Class<?>) types[0];
        } else {
            return field.getType();
        }
    }

    //    @Cacheable(cacheNames = "jpaRepository")
    public JpaRepository<?, ?> getJpaRepository(Class<?> actualType) {
        for (JpaRepository<?, Long> jpaRepository : jpaRepositoryList) {
            Class<?> genericInterface = (Class<?>) jpaRepository.getClass().getGenericInterfaces()[0];
            ParameterizedType parameterizedType = (ParameterizedType) genericInterface.getGenericInterfaces()[0];
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments[0] == actualType) {
                return jpaRepository;
            }
        }
        return null;
    }
}
