package summer;

import summer.config.Config;
import summer.config.ConfigurationClass;
import summer.config.impl.CoreConfig;
import summer.config.impl.JavaConfig;
import summer.core.Context;
import summer.core.impl.ApplicationContext;
import summer.core.impl.ObjectFactoryImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Содержит в себе контекст приложения. Отвечает за его создание.
 */
public class Application {

    private static Context context;

    /**
     * Создание и сохранение контекста из конфигурационных классов.
     * @param configClasses клссы конфигурации
     * @return контекст
     */
    @SafeVarargs
    public static Context createContextFromConfigs(Class<? extends ConfigurationClass>... configClasses) {
        if (context != null) {
            throw new RuntimeException("Context already exists");
        }

        List<Class<? extends ConfigurationClass>> classes = new ArrayList<>();

        classes.add(CoreConfig.class);
        if (configClasses != null && configClasses.length > 0) {
            List<Class<? extends ConfigurationClass>> cfgList = Arrays.asList(configClasses);
            cfgList.remove(CoreConfig.class);
            classes.addAll(cfgList);
        }

        Config config = new JavaConfig(classes.toArray(new Class[0]));
        Context context = new ApplicationContext(config);

        ObjectFactoryImpl objectFactoryImpl = new ObjectFactoryImpl(context);
        context.setFactory(objectFactoryImpl);

        Application.context = context;

        return context;
    }

    /**
     * Создание контекста по умолчанию
     * @return контекст
     */
    private static Context defaultContext() {
        return createContextFromConfigs();
    }

    /**
     * Получение контекста
     * @return конеткст
     */
    public static Context getContext() {
        if (context == null)
            context = defaultContext();
        return context;
    }

}
