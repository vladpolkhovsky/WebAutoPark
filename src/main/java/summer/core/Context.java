package summer.core;

import summer.config.Config;

public interface Context {

    /**
     * Создание объекта указанного класса. Причём будет выбранна реадизация из Config
     * @param type целевой класс
     * @param <T> параметр класса
     * @return настроенный объект
     */
    <T> T getObject(Class<T> type);

    /**
     * Получение конфигуации
     * @return конфигурация
     */
    Config getConfig();

}
