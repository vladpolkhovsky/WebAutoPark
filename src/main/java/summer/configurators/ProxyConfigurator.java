package summer.configurators;

import summer.core.Context;

/**
 * Создаёт прокси объекта
 */
public interface ProxyConfigurator {

    /**
     * Создание прокси
     *
     * @param object         целевой для проксирования объект
     * @param implementation целевая реализация
     * @param context        контекст приложения
     * @param <T>            целевой класс
     * @return прокси объекта
     */
    <T> T makeProxy(T object, Class<T> implementation, Context context);

}
