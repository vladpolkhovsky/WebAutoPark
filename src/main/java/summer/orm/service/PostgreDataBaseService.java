package summer.orm.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import summer.core.annotations.Autowired;
import summer.core.annotations.InitMethod;
import summer.orm.ConnectionFactory;
import summer.orm.SqlFieldType;
import summer.orm.annotations.Column;
import summer.orm.annotations.ID;
import summer.orm.annotations.Table;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PostgreDataBaseService {

    @Autowired
    private ConnectionFactory connectionFactory;

    private Map<String, String> classToSql;

    private static final String CHECK_TABLE_SQL_PATTERN = "SELECT EXISTS (\n" +
            "   SELECT FROM information_schema.tables \n" +
            "   WHERE  table_schema = 'public'\n" +
            "   AND    table_name   = '%s'\n" +
            ");";

    private static final String CREATE_TABLE_SQL_PATTERN = "CREATE TABLE %s (\n" +
            "    %s integer PRIMARY KEY" +
            "%s\n);";

    @InitMethod
    public void init() {
        classToSql = Arrays.asList(SqlFieldType.values()).stream().collect(Collectors.toMap(
                sqlFieldType -> sqlFieldType.getType().getName(), SqlFieldType::getSqlType
        ));
    }

    @SneakyThrows
    public boolean isTableExist(String tableName) {
        Connection connection = connectionFactory.getConnection();
        String sql = String.format(CHECK_TABLE_SQL_PATTERN, tableName);
        log.info("SQL -> {}", sql);
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        resultSet.next();
        return resultSet.getBoolean("exists");
    }

    @SneakyThrows
    public void createTable(Class<?> type) {
        validateTable(type);
        String tableName = type.getAnnotation(Table.class).name();
        String idField = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ID.class))
                .findFirst().get().getName();
        String sql = String.format(CREATE_TABLE_SQL_PATTERN, tableName, idField, makeFieldsSql(type));
        log.info("SQL -> {}", sql);
        connectionFactory.getConnection().createStatement().execute(sql);
    }

    @SneakyThrows
    private String makeFieldsSql(Class<?> type) {
        String fields = "";
        for (Field declaredField : type.getDeclaredFields()) {
            Column column = declaredField.getAnnotation(Column.class);
            if (column != null)
                fields += fieldToSql(declaredField, column);
        }
        return fields;
    }

    private String fieldToSql(Field field, Column column) {
        String sqlFiled = ",\n    ";
        sqlFiled += column.name() + " " + classToSql.get(field.getType().getName());
        if (column.nullable())
            sqlFiled += " NOT NULL";
        if (column.unique())
            sqlFiled += " UNIQUE";
        return sqlFiled;
    }

    private void validateTable(Class<?> type) {
        validateId(type);
    }

    private void validateId(Class<?> type) {
        List<Field> idFields = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ID.class))
                .collect(Collectors.toList());
        if (idFields.size() != 1)
            throw new RuntimeException(String.format("Class %s contains illegal count @ID fields (0 or more then 1)", type.getName()));
        Field idField = idFields.iterator().next();
        if (idField.getType() != Long.class)
            throw new RuntimeException(String.format("@ID field '%s' has illegal type. Use Long type", idField.getName()));
    }
}
