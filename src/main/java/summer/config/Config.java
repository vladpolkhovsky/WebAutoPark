package summer.config;

import summer.core.Scanner;

/**
 * Отвечает за настройку контекста. Содержит в себе все реализации интерфейсов.
 */
public interface Config {

    /**
     * Возращает реализацию данного интерфейса
     * @param target интерфейс
     * @return реализация интерфейса
     */
    Class<?> getImplementation(Class<?> target);

    /**
     * Вернёт объект сканнер
     * @return объект сканнер
     */
    Scanner getClassScanner();

}
