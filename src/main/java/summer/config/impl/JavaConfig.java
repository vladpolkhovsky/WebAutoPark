package summer.config.impl;

import lombok.AllArgsConstructor;
import summer.config.Config;
import summer.core.Scanner;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class JavaConfig implements Config {

    private final Scanner scanner;

    private final Map<Class<?>, Class<?>> interfaceToImplementation;

    @Override
    public <T> Class<? extends T> getImplementation(Class<T> target) {
        Set<Class<? extends T>> classes = scanner.getSubTypesOf(target);
        if (classes.size() != 1) {
            if (!interfaceToImplementation.containsKey(target))
                throw new RuntimeException("target interface has 0 or more then one impl");
            classes.clear();
            classes.add((Class<? extends T>) interfaceToImplementation.get(target));
        }
        return classes.iterator().next();
    }

    @Override
    public Scanner getScanner() {
        return scanner;
    }
}












