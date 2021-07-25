package summer.core.impl;

import lombok.SneakyThrows;
import summer.configurators.ObjectConfigurator;
import summer.core.Context;
import summer.core.ObjectFactory;
import summer.configurators.ProxyConfigurator;
import summer.core.annotations.InitMethod;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class ObjectFactoryImpl implements ObjectFactory {

    private final Context context;

    private final List<ObjectConfigurator> objectConfigurators = new ArrayList<>();

    private final List<ProxyConfigurator> proxyConfigurators = new ArrayList<>();

    @SneakyThrows
    public ObjectFactoryImpl(Context context) {
        this.context = context;
        for (Class<?> clazz : context.getConfig()
                .getClassScanner().getSubTypesOf(ObjectConfigurator.class)) {
            objectConfigurators.add((ObjectConfigurator) clazz.getDeclaredConstructor().newInstance());
        }
        for (Class<?> clazz : context.getConfig()
                .getClassScanner().getSubTypesOf(ProxyConfigurator.class)) {
            proxyConfigurators.add((ProxyConfigurator) clazz.getDeclaredConstructor().newInstance());
        }
    }


    @SneakyThrows
    public <T> T createObject(Class<T> implementation) {
        T object = create(implementation);
        configure(object);
        initialize(implementation, object);
        object = makeProxy(implementation, object);
        return object;
    }

    private <T> T makeProxy(Class<T> implClass, T object) {
        for (ProxyConfigurator proxyConfigurator : proxyConfigurators) {
            object = (T) proxyConfigurator.makeProxy(object, implClass);
        }
        return object;
    }

    private <T> void initialize(Class<T> implementation, T object) throws Exception {
        for (Method method : implementation.getMethods()) {
            if (method.isAnnotationPresent(InitMethod.class)) {
                method.invoke(object);
            }
        }
    }

    private <T> void configure(T object) {
        objectConfigurators.forEach(objectConfigurator -> objectConfigurator.configure(object, context));
    }

    private <T> T create(Class<T> implementation) throws Exception {
        return implementation.getDeclaredConstructor().newInstance();
    }
}




