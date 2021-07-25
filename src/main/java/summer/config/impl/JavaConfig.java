package summer.config.impl;

import lombok.SneakyThrows;
import summer.config.Config;
import summer.config.ConfigurationClass;
import summer.core.Scanner;

import java.util.*;
import java.util.stream.Collectors;

public class JavaConfig implements Config {

    private Scanner classScanner;

    private Map<Class<?>, Class<?>> interfaceToImplementation = new HashMap<>();

    @SneakyThrows
    public JavaConfig(Class<? extends ConfigurationClass>[] configClasses) {
        classScanner = scannerFromArray(configClasses);
    }

    private Scanner scannerFromArray(Class<? extends ConfigurationClass>[] configClasses) throws Exception {

        Map<Class<?>, List<Class<?>>> manyVars = new HashMap<>();

        for (Class<? extends ConfigurationClass> configClass : configClasses) {
            configClass.newInstance().addConfigurations(manyVars);
        }

        Map<Class<?>, Class<?>> oneVar = new HashMap<>();

        for (Class<? extends ConfigurationClass> configClass : configClasses) {
            configClass.newInstance().addConfiguration(oneVar);
        }

        manyVars.putAll(
                oneVar.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> Collections.singletonList(e.getValue())))
        );

        return new Scanner() {
            @Override
            public Set<Class<?>> getSubTypesOf(Class<?> type) {
                if (manyVars.get(type) == null)
                    return new HashSet<>();
                return new HashSet<>(manyVars.get(type));
            }
        };

    }

    @Override
    public Class<?> getImplementation(Class<?> targetInterface) {
        return interfaceToImplementation.computeIfAbsent(targetInterface, aClass -> {
            Set<Class<?>> classes = classScanner.getSubTypesOf(targetInterface);
            if (classes.size() != 1) {
                throw new RuntimeException(targetInterface + " has 0 or more than one impl please update your config");
            }
            return classes.iterator().next();
        });
    }

    @Override
    public Scanner getClassScanner() {
        return classScanner;
    }

}












