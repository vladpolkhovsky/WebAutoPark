package summer.config;

import java.util.List;
import java.util.Map;

public interface ConfigurationClass {

    default void addConfigurations(Map<Class<?>, List<Class<?>>> configuration) {

    }

    default void addConfiguration(Map<Class<?>, Class<?>> configuration) {

    }

}
