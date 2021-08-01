package summer.core;

import java.util.Set;

public interface Scanner {

    /**
     * Возращает реализации данного интерфейса
     * @param type класс предок для реадизаций
     * @return Все реализации данного класса
     */
    Set<Class<?>> getSubTypesOf(Class<?> type);

}
