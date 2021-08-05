package summer.core;

public interface Cache {

    /**
     * Проверяет находится ли объект в кэше
     *
     * @param clazz класс объекта
     * @return true если объект в кэше, иначе false
     */
    boolean contains(Class<?> clazz);

    /**
     * Получить объект из кэша
     *
     * @param clazz класс объекта
     * @param <T> параметр
     * @return объект если он в кэше, иначе null
     */
    <T> T get(Class<T> clazz);


    /**
     * Поместит объект в кэш
     *
     * @param clazz целевой класс
     * @param value обхект класса clazz
     */
    <T> void put(Class<T> clazz, T value);

}
