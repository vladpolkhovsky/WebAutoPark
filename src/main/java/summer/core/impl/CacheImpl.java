package summer.core.impl;

import summer.core.Cache;

import java.util.HashMap;
import java.util.Map;

public class CacheImpl implements Cache {

    private Map<String, Object> cache;

    public CacheImpl() {
        this.cache = new HashMap<>();
    }

    @Override
    public boolean contains(Class<?> clazz) {
        return cache.containsKey(clazz.getName());
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return (T) cache.get(clazz.getName());
    }

    @Override
    public <T> void put(Class<T> target, T value) {
        cache.put(target.getName(), value);
    }
}
