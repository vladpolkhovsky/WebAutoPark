package summer.configurators;

import summer.core.Context;

public interface ObjectConfigurator {

    /**
     * Создаёт прокси объекта
     *
     * @param Object объект для проксирования
     * @param context контекст
     */
    void configure(Object Object, Context context);

}
