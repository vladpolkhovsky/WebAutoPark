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

public class Application {

    private static Context context;

    public static Context createContextFromConfigs(Class<? extends ConfigurationClass>... configClasses) {
        if (context != null) {
            throw new RuntimeException("Context already exists");
        }

        List<Class<? extends ConfigurationClass>> classes = new ArrayList<>();

        classes.add(CoreConfig.class);
        if (configClasses != null && configClasses.length > 0) {
            classes.addAll(Arrays.asList(configClasses));
        }

        Config config = new JavaConfig(classes.toArray(new Class[0]));
        Context context = new ApplicationContext(config);

        ObjectFactoryImpl objectFactoryImpl = new ObjectFactoryImpl(context);
        context.setFactory(objectFactoryImpl);

        Application.context = context;

        return context;
    }

    private static Context defaultContext() {
        return createContextFromConfigs(CoreConfig.class);
    }

    public static Context getContext() {
        if (context == null)
            context = defaultContext();
        return context;
    }

}
