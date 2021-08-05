package summer.core.impl;

import summer.config.Config;
import summer.config.impl.JavaConfig;
import summer.core.Cache;
import summer.core.Context;
import summer.core.ObjectFactory;
import summer.core.Scanner;

import java.util.Map;

public class ApplicationContext implements Context {

    private final Config config;

    private final ObjectFactory factory;

    private final Cache cache;

    public ApplicationContext(String packageToScan, Map<Class<?>, Class<?>> interfaceToImplementation) {
        this.config = new JavaConfig(new ScannerImpl(packageToScan), interfaceToImplementation);
        this.cache = new CacheImpl();
        this.factory = new ObjectFactoryImpl(this);
    }

    @Override
    public <T> T getObject(Class<T> type) {
        if (cache.contains(type))
            return cache.get(type);
        Class<? extends T> impl = type;
        if (impl.isInterface())
            impl = config.getImplementation(type);
        T t = factory.createObject(impl);
        cache.put(type, t);
        return t;
    }

    @Override
    public Config getConfig() {
        return config;
    }

}
