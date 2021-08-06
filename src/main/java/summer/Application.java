package summer;

import summer.core.Context;
import summer.core.impl.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Содержит в себе контекст приложения. Отвечает за его создание.
 */
public class Application {

    private static Context context;

    private Application() {

    }

    public static Context createContext(Map<Class<?>, Class<?>> interfaceToImplementation) {
        if (context == null)
            context = new ApplicationContext("", interfaceToImplementation);
        return context;
    }

    public static Context createContext() {
        if (context == null)
            context = new ApplicationContext("", new HashMap<>());
        return context;
    }

    public static Context getContext() {
        if (context == null)
            throw new RuntimeException("No context configured.");
        return context;
    }

}
