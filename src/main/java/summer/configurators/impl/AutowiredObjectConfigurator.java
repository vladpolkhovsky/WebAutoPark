package summer.configurators.impl;

import lombok.SneakyThrows;
import summer.configurators.ObjectConfigurator;
import summer.core.Context;
import summer.core.annotations.Autowired;

import java.lang.reflect.Field;

public class AutowiredObjectConfigurator implements ObjectConfigurator {
    @Override
    @SneakyThrows
    public void configure(Object t, Context context) {
        for (Field field : t.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                Object object = context.getObject(field.getType());
                field.set(t, object);
            }
        }
    }
}
