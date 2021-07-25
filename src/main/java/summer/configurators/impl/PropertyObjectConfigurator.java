package summer.configurators.impl;

import jdk.nashorn.api.scripting.URLReader;
import summer.core.annotations.Property;
import lombok.SneakyThrows;
import summer.configurators.ObjectConfigurator;
import summer.core.Context;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class PropertyObjectConfigurator implements ObjectConfigurator {

    private Map<String, String> properties;

    @SneakyThrows
    public PropertyObjectConfigurator() {
        URL path = this.getClass().getClassLoader().getResource("application.properties");
        Stream<String> lines = new BufferedReader(new URLReader(path)).lines();
        properties = lines.map(line -> line.split("=")).collect(toMap(arr -> arr[0], arr -> arr[1]));
    }

    @Override
    @SneakyThrows
    public void configure(Object t, Context context) {
        Class<?> clazz = t.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Property annotation = field.getAnnotation(Property.class);
            if (annotation != null) {
                String value = annotation.value().isEmpty() ?
                        properties.get(field.getName()) : properties.get(annotation.value());
                field.setAccessible(true);
                field.set(t, value);
            }
        }
    }
}
