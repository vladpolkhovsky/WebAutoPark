package summer.orm.impl;

import lombok.SneakyThrows;
import summer.core.annotations.InitMethod;
import summer.core.annotations.Property;
import summer.orm.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactoryImpl implements ConnectionFactory {

    @Property("url")
    private String url;

    @Property("username")
    private String username;

    @Property("password")
    private String password;

    private Connection connection;

    @SneakyThrows
    @InitMethod
    public void initConnection() {
        connection = DriverManager.getConnection(url, username, password);
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

}
