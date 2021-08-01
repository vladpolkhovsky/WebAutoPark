package summer.configurators.impl;

import lombok.SneakyThrows;
import summer.configurators.ObjectConfigurator;
import summer.core.Context;
import summer.core.annotations.ClassForName;

import java.lang.reflect.Field;

public class ClassForNameObjectConfigurator implements ObjectConfigurator {
    @Override
    @SneakyThrows
    public void configure(Object t, Context context) {
        for (Field field : t.getClass().getDeclaredFields()) {
            ClassForName classForName = field.getAnnotation(ClassForName.class);
            if (classForName != null) {
                if (classForName.value().isEmpty())
                    throw new RuntimeException("Empty class name in field " + field.getName());
                field.setAccessible(true);
                Object object = Class
                        .forName(classForName.value())
                        .getDeclaredConstructor()
                        .newInstance();
                field.set(t, object);
            }
        }
    }
}
