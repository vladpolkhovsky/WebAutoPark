package summer.orm.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import summer.core.Context;
import summer.core.annotations.Autowired;
import summer.core.annotations.InitMethod;
import summer.orm.ConnectionFactory;
import summer.orm.annotations.Column;
import summer.orm.annotations.ID;
import summer.orm.annotations.Table;
import summer.orm.enums.SqlFieldInsertPattern;
import summer.orm.enums.SqlFieldType;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class PostgreDataBaseService {

    @Autowired
    private ConnectionFactory connectionFactory;

    private Map<String, String> classToSql;

    private Map<String, String> insertPatternByClass;

    private Map<String, String> insertByClassPattern;

    @Autowired
    private Context context;

    private static final String CHECK_TABLE_SQL_PATTERN = "SELECT EXISTS (\n" +
            "   SELECT FROM information_schema.tables \n" +
            "   WHERE  table_schema = 'public'\n" +
            "   AND    table_name   = '%s'\n" +
            ");";

    private static final String CREATE_TABLE_SQL_PATTERN = "CREATE TABLE %s (\n" +
            "    %s integer PRIMARY KEY DEFAULT nextval('%s')" +
            "%s\n);";

    private static final String SEQ_NAME = "ID_SEQ";

    private static final String CREATE_ID_SEQ_PATTERN = "CREATE SEQUENCE %S\n" +
            "    INCREMENT 1\n" +
            "    START 1;";

    private static final String INSERT_SQL_PATTERN = "INSERT INTO %s(%s)\n" +
            "    VALUES (%s)\n" +
            "    RETURNING %s";

    @InitMethod
    public void init() {
        classToSql = Arrays.stream(SqlFieldType.values()).collect(Collectors.toMap(
                sqlFieldType -> sqlFieldType.getType().getName(), SqlFieldType::getSqlType
        ));
        insertPatternByClass = Arrays.stream(SqlFieldInsertPattern.values()).collect(Collectors.toMap(
                sqlFieldType -> sqlFieldType.getType().getName(), SqlFieldInsertPattern::getSqlType
        ));
        Set<Class<?>> typesAnnotatedWithTable = context.getConfig()
                .getScanner().getReflections().getTypesAnnotatedWith(Table.class);
        typesAnnotatedWithTable.forEach(this::validateTable);
        insertByClassPattern = typesAnnotatedWithTable.stream().collect(Collectors.toMap(
                Class::getName, this::makeInsertPattern
        ));
    }

    private String makeInsertPattern(Class<?> clazz) {
        StringBuilder insertFields = new StringBuilder();
        StringBuilder values = new StringBuilder();
        AtomicBoolean isFirst = new AtomicBoolean(false);
        AtomicReference<String> idFieldName = new AtomicReference<>("");
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            if (field.isAnnotationPresent(ID.class))
                idFieldName.set(field.getName());
            else {
                if (isFirst.get()) {
                    insertFields.append(", ");
                    values.append(", ");
                }
                insertFields.append(field.getAnnotation(Column.class).name());
                values.append(insertPatternByClass.get(field.getType().getName()));
                isFirst.set(true);
            }
        });
        String sql = String.format(INSERT_SQL_PATTERN,
                clazz.getAnnotation(Table.class).name(),
                insertFields.toString(),
                values.toString(),
                idFieldName.get()
        );
        log.info("Insert sql for class {} -> {}", clazz.getName(), sql);
        return sql;
    }

    @SneakyThrows
    public boolean isTableExist(String tableName) {
        Connection connection = connectionFactory.getConnection();
        String sql = String.format(CHECK_TABLE_SQL_PATTERN, tableName);
        log.info("SQL -> {}", sql);
        ResultSet resultSet = connection.createStatement().executeQuery(sql);
        resultSet.next();
        boolean exists = resultSet.getBoolean("exists");
        if (!exists) {
            String sqlSeq = String.format(CREATE_ID_SEQ_PATTERN, SEQ_NAME);
            log.info("SQL -> {}", sqlSeq);
            connectionFactory.getConnection().createStatement().execute(sqlSeq);
        }
        return exists;
    }

    @SneakyThrows
    public void createTable(Class<?> type) {
        String tableName = type.getAnnotation(Table.class).name();
        String idField = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ID.class))
                .findFirst().get().getName();
        String sql = String.format(CREATE_TABLE_SQL_PATTERN, tableName, idField, SEQ_NAME, makeFieldsSql(type));
        log.info("SQL -> {}", sql);
        connectionFactory.getConnection().createStatement().execute(sql);
    }

    @SneakyThrows
    public Long save(Object value) {
        String sql = String.format(insertByClassPattern.get(value.getClass().getName()), prepareValues(value));
        log.info("SQL -> {}", sql);
        ResultSet resultSet = connectionFactory.getConnection().createStatement().executeQuery(
                sql
        );
        resultSet.next();
        Field idField = Arrays.stream(value.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ID.class))
                .findFirst().get();
        Long id = resultSet.getLong(idField.getName());
        idField.setAccessible(true);
        idField.set(value, id);
        return id;
    }

    @SneakyThrows
    private Object[] prepareValues(Object value) {
        Object[] objects = Arrays.stream(value.getClass().getDeclaredFields()).filter(
                field -> field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(ID.class)
        ).map(field -> {
            try {
                field.setAccessible(true);
                return field.get(value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }).toArray();
        return objects;
    }

    @SneakyThrows
    private String makeFieldsSql(Class<?> type) {
        StringBuilder fields = new StringBuilder();
        for (Field declaredField : type.getDeclaredFields()) {
            Column column = declaredField.getAnnotation(Column.class);
            if (column != null)
                fields.append(fieldToSql(declaredField, column));
        }
        return fields.toString();
    }

    private String fieldToSql(Field field, Column column) {
        StringBuilder sqlFiled = new StringBuilder(",\n    ");
        sqlFiled.append(column.name()).append(" ").append(classToSql.get(field.getType().getName()));
        if (column.nullable())
            sqlFiled.append(" NOT NULL");
        if (column.unique())
            sqlFiled.append(" UNIQUE");
        return sqlFiled.toString();
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
