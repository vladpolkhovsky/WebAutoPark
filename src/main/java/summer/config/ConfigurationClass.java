package summer.config;

import java.util.List;
import java.util.Map;

/**
 * Конфигурация, методы необходимо переопределить, если требуется добавить сторонние аннотации или
 * уточнить реализацию интерейса.
 */
public interface ConfigurationClass {

    default void addConfigurations(Map<Class<?>, List<Class<?>>> configuration) {

    }

    default void addConfiguration(Map<Class<?>, Class<?>> configuration) {

    }

}
