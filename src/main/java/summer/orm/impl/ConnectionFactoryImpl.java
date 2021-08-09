package summer.orm.impl;

import lombok.SneakyThrows;
import summer.core.annotations.InitMethod;
import summer.core.annotations.Property;
import summer.orm.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Инициализация и получение соединения с БД
 */
public class ConnectionFactoryImpl implements ConnectionFactory {

    /**
     * URL базы данных
     */
    @Property("url")
    private String url;

    /**
     * имя пользователя
     */
    @Property("username")
    private String username;

    /**
     * пароль
     */
    @Property("password")
    private String password;

    private Connection connection;

    /**
     * Инициадизация соединения с БД
     */
    @SneakyThrows
    @InitMethod
    public void initConnection() {
        connection = DriverManager.getConnection(url, username, password);
    }

    /**
     * Получения соединения
     *
     * @return Connection
     */
    @Override
    public Connection getConnection() {
        return connection;
    }

}
