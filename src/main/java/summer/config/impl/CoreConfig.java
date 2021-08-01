package summer.config.impl;

import summer.config.ConfigurationClass;
import summer.configurators.ObjectConfigurator;
import summer.configurators.impl.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Необходимый минимум для работы фреймворка. Подключается автоматически.
 */
public final class CoreConfig implements ConfigurationClass {

    @Override
    public void addConfigurations(Map<Class<?>, List<Class<?>>> configuration) {
        configuration.put(
                ObjectConfigurator.class, Arrays.asList(
                        AutowiredObjectConfigurator.class,
                        PropertyObjectConfigurator.class,
                        ClassForNameObjectConfigurator.class
                )
        );
    }

    @Override
    public void addConfiguration(Map<Class<?>, Class<?>> configuration) {

    }
}
