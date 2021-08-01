package summer.core.impl;

import summer.core.annotations.Singleton;
import summer.config.Config;
import summer.core.Context;
import summer.core.ObjectFactory;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext implements Context {

    private ObjectFactory factory;

    private final Map<Class<?>, Object> cache = new HashMap<>();

    private final Config config;

    public ApplicationContext(Config config) {
        this.config = config;
    }

    @Override
    public <T> T getObject(Class<? extends T> type) {
        //Проверка кэша
        if (cache.containsKey(type)) {
            return (T)cache.get(type);
        }
        //Получение реализации, если интерфейс
        Class<?> implementation = type;
        if (type.isInterface()) {
            implementation = config.getImplementation(type);
        }
        //Создание и сохранение в кэше, если Singleton
        Object object = factory.createObject(implementation);
        if (implementation.isAnnotationPresent(Singleton.class)) {
            cache.put(type, object);
        }
        return (T)object;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public void setFactory(ObjectFactory factory) {
        this.factory = factory;
    }

}
