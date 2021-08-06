package summer.orm;

import java.sql.Connection;

public interface ConnectionFactory {

    /**
     * Получить соединение к БД
     *
     * @return Connection
     */
    Connection getConnection();

}
