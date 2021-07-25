package app;

import summer.Application;
import summer.config.ConfigurationClass;
import summer.core.Context;

import java.util.List;
import java.util.Map;

/**
 * @author Evgeny Borisov
 */
public class Main {

    public static class Configuration implements ConfigurationClass {

    }

    public static void main(String[] args) {
        Context context = Application.createContextFromConfigs(Configuration.class);

    }
}
