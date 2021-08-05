package summer;

import summer.core.Context;
import summer.core.impl.ApplicationContext;

import java.util.Map;

/**
 * Содержит в себе контекст приложения. Отвечает за его создание.
 */
public class Application {

    private static Context context;

    public static Context getContext(Map<Class<?>, Class<?>> interfaceToImplementation) {
        if (context == null)
            context = new ApplicationContext("", interfaceToImplementation);
        return context;
    }

}
