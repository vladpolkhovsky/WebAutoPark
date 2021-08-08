package summer.orm;

import java.util.List;

public interface EntityManager {

    /**
     * Получение объекта по полю помеченному как @ID
     *
     * @param id    первичный ключ
     * @param clazz класс объекта
     * @param <T>   параметр класса
     * @return объект с Указанным id из БД
     */
    <T> T get(Long id, Class<T> clazz);

    /**
     * Сохранение объекта в БД
     *
     * @param object
     */
    Long save(Object object);


    /**
     * Получение всех объектов из таблицы @Table
     *
     * @param clazz класс объекта
     * @param <T>   параметр класса
     * @return все объекты из таблицы
     */
    <T> List<T> getAll(Class<T> clazz);

}
