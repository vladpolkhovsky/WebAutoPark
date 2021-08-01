package summer.core;

public interface ObjectFactory {

    /**
     * Создание объекта указанного класса, его настройка и проксирование
     * @param implementation класс реализацю которого необходимо получить
     * @param <T> целевой класс
     * @return настроенный и проксированный объект
     */
    <T> T createObject(Class<T> implementation);

}
