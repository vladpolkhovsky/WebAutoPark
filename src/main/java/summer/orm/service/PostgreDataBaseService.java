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
import summer.orm.enums.SqlFieldType;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Манипуляция базой данных PostgreSQL
 */
@Slf4j
public class PostgreDataBaseService {

    /**
     * Получение соединения с БД
     */
    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * Map JavaClass против SQL Type
     */
    private Map<String, String> classToSql;

    /**
     * Мапа JavaClass против патерна вставки
     */
    private Map<String, String> insertPatternByClass;

    /**
     * Мапа JavaClass против патерна вставки в БД
     */
    private Map<String, String> insertByClassPattern;

    /**
     * Контекст
     */
    @Autowired
    private Context context;

    /**
     * Запрос на определение того что таблица существет в БД
     */
    private static final String CHECK_TABLE_SQL_PATTERN = "SELECT EXISTS (\n" +
            "   SELECT FROM information_schema.tables \n" +
            "   WHERE  table_schema = 'public'\n" +
            "   AND    table_name   = '%s'\n" +
            ");";

    /**
     * Запрос на определение того что последовательность существет в БД
     */
    private static final String CHECK_SEQ_SQL_PATTERN = "SELECT EXISTS (\n" +
            "   SELECT FROM information_schema.sequences \n" +
            "   WHERE  sequence_schema = 'public'\n" +
            "   AND    sequence_name   = '%s'\n" +
            ");";

    /**
     * Запрос на создание таблицы в БД
     */
    private static final String CREATE_TABLE_SQL_PATTERN = "CREATE TABLE %s (\n" +
            "    %s integer PRIMARY KEY DEFAULT nextval('%s')" +
            "%s\n);";

    /**
     * Название последоваельности идентефикаторов
     */
    private static final String SEQ_NAME = "id_seq";

    /**
     * Запрос на создание последоватеьности
     */
    private static final String CREATE_ID_SEQ_PATTERN = "CREATE SEQUENCE %S\n" +
            "    INCREMENT 1\n" +
            "    START 1;";

    /**
     * Запрос на вставку в БД
     */
    private static final String INSERT_SQL_PATTERN = "INSERT INTO %s(%s)\n" +
            "    VALUES (%s)\n" +
            "    RETURNING %s";

    /**
     * Инициализация сервиса
     */
    @InitMethod
    public void init() {
        // java.lang.Long -> integer
        classToSql = Arrays.stream(SqlFieldType.values()).collect(Collectors.toMap(
                sqlFieldType -> sqlFieldType.getType().getName(), SqlFieldType::getSqlType
        ));

        // java.lang.Long -> %s, java.lang.String -> '%s'
        insertPatternByClass = Arrays.stream(SqlFieldType.values()).collect(Collectors.toMap(
                sqlFieldType -> sqlFieldType.getType().getName(), SqlFieldType::getInsertPattern
        ));

        //Создание последовательнсоти
        validateSeq();

        //Получение всех объектов-таблиц и их создание
        Set<Class<?>> typesAnnotatedWithTable = context.getConfig()
                .getScanner().getReflections().getTypesAnnotatedWith(Table.class);

        //Валидация таблиц
        typesAnnotatedWithTable.forEach(this::validateTable);

        for (Class<?> type : typesAnnotatedWithTable) {
            boolean tableExists = isTableExist(type.getAnnotation(Table.class).name());
            log.info("Class {} annotated with @Table. Table exists -> {}",
                    type.getName(), tableExists);
            if (!tableExists)
                createTable(type);
        }

        //Создание запросов вставки для таблиц
        insertByClassPattern = typesAnnotatedWithTable.stream().collect(Collectors.toMap(
                Class::getName, this::makeInsertPattern
        ));
    }

    /**
     * Создание таблицы
     *
     * @param type целевой класс-таблица
     */
    @SneakyThrows
    public void createTable(Class<?> type) {
        String tableName = type.getAnnotation(Table.class).name();
        String idField = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ID.class))
                .findFirst().get().getName();

        String sql = String.format(CREATE_TABLE_SQL_PATTERN, tableName, idField, SEQ_NAME, makeFieldsSql(type));
        log.info("SQL -> {}", sql);

        Statement statement = connectionFactory.getConnection().createStatement();
        statement.execute(sql);
        statement.close();
    }

    /**
     * Сохранение объекта в БД
     *
     * @param value объект
     * @return @ID поле объекта
     */
    @SneakyThrows
    public Long save(Object value) {
        String sql = String.format(insertByClassPattern.get(value.getClass().getName()), prepareValues(value));
        log.info("SQL -> {}", sql);

        Statement statement = connectionFactory.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        Field idField = Arrays.stream(value.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ID.class))
                .findFirst().get();
        Long id = resultSet.getLong(idField.getName());
        statement.close();

        idField.setAccessible(true);
        idField.set(value, id);
        return id;
    }

    /**
     * Получение объекта по id
     *
     * @param id    id
     * @param clazz класс-таблица
     * @param <T>   целевой класс
     * @return объект класса
     */
    @SneakyThrows
    public <T> Optional<T> get(Long id, Class<T> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null)
            throw new RuntimeException("Class not annotated with @Table");

        Field idField = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ID.class)).findAny().get();

        String sql = "SELECT * FROM " + table.name() + " WHERE " + idField.getName() + " = " + id;

        log.info("SQL -> {}", sql);

        Statement statement = connectionFactory.getConnection().createStatement();
        ResultSet result = statement.executeQuery(sql);
        if (result.next())
            return Optional.of(makeObject(result, clazz));
        statement.close();

        return Optional.empty();
    }

    /**
     * Получение всех объектов из таблицы
     *
     * @param clazz целевой класс-таблица
     * @param <T>   класс
     * @return лист записей таблицы
     */
    @SneakyThrows
    public <T> List<T> getAll(Class<T> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null)
            throw new RuntimeException("Class not annotated with @Table");
        String sql = "SELECT * FROM " + table.name();
        log.info("SQL -> {}", sql);
        List<T> list = new ArrayList<>();
        Statement statement = connectionFactory.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            list.add(makeObject(resultSet, clazz));
        }
        statement.close();
        return list;
    }

    /**
     * Создание объекта из ResultSet
     *
     * @param resultSet строка таблицы
     * @param clazz     целевой объект
     * @param <T>       класс
     * @return созданный объект
     */
    @SneakyThrows
    private <T> T makeObject(ResultSet resultSet, Class<T> clazz) {
        Field idField = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ID.class)).findAny().get();

        List<Field> fieldList = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .collect(Collectors.toList());

        T object = clazz.getDeclaredConstructor().newInstance();

        idField.setAccessible(true);
        idField.set(object, resultSet.getLong(idField.getName()));

        for (Field field : fieldList) {
            field.setAccessible(true);
            Column column = field.getAnnotation(Column.class);
            field.set(object, resultSet.getObject(column.name(), field.getType()));
        }

        return object;
    }

    /**
     * Подготовка полей объекта к вставке в таблицу 'values(%s, '%s') -> values(15, 'abc')'
     *
     * @param value объект
     * @return массив значений для вставки
     */
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

    /**
     * Переводит поля класса-таблицы в тело таблицы fields... : 'create table name(fields.....)'
     *
     * @param type класс-таблицы
     * @return строка шаблон
     */
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

    /**
     * Переводит поле с аннотацией @Column в вид 'Long -> integer NOT NULL UNIQUE'
     *
     * @param field поле
     * @param column аннотация поля
     * @return строкое прелдставлнеие для Sql
     */
    private String fieldToSql(Field field, Column column) {
        StringBuilder sqlFiled = new StringBuilder(",\n    ");
        sqlFiled.append(column.name()).append(" ").append(classToSql.get(field.getType().getName()));
        if (column.nullable())
            sqlFiled.append(" NOT NULL");
        if (column.unique())
            sqlFiled.append(" UNIQUE");
        return sqlFiled.toString();
    }

    /**
     * Валидация класс-таблицы
     *
     * @param type
     */
    private void validateTable(Class<?> type) {
        if (type.getAnnotation(Table.class).name().isEmpty())
            throw new RuntimeException("Table " + type.getName() + " has empty name");
        validateId(type);
        validateColumns(type);
    }

    /**
     * Проверка колонок на совпадение имён и на допустимость типов
     *
     * @param type класс-таблица
     */
    private void validateColumns(Class<?> type) {
        Set<String> validateColumns = new HashSet<>();
        List<Field> columns = Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ID.class) || field.isAnnotationPresent(Column.class))
                .collect(Collectors.toList());
        columns.forEach(field -> {
            if (!insertPatternByClass.containsKey(field.getType().getName()))
                throw new RuntimeException("Table " + type.getName() + " has column with illegal type " + field.getType().getName());
            ID id = field.getAnnotation(ID.class);
            Column column = field.getAnnotation(Column.class);
            String name = id == null ? column.name() : field.getName();
            if (validateColumns.contains(name))
                throw new RuntimeException("Table " + type.getName() + " has 2 or more columns with same name '" + name + "'");
            validateColumns.add(name);
        });
    }

    /**
     * Проверка поля ID.
     *
     * @param type
     */
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

    /**
     * Проверка последовательности на существование и её создание
     */
    @SneakyThrows
    private void validateSeq() {
        if (!isSeqExist()) {
            String sqlSeq = String.format(CREATE_ID_SEQ_PATTERN, SEQ_NAME);
            log.info("SQL -> {}", sqlSeq);
            Statement statement = connectionFactory.getConnection().createStatement();
            statement.execute(sqlSeq);
            statement.close();
        }
    }

    /**
     * Проверка последовательности на существование
     *
     * @return true если последовательность существует
     */
    @SneakyThrows
    private boolean isSeqExist() {
        Statement statement = connectionFactory.getConnection().createStatement();
        String sql = String.format(CHECK_SEQ_SQL_PATTERN, SEQ_NAME);
        log.info("SQL -> {}", sql);
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        boolean exists = resultSet.getBoolean("exists");
        log.info("Sequence {}. Sequence exists -> {}", SEQ_NAME, exists);
        statement.close();
        return exists;
    }

    /**
     * Проверка таблицы на существование
     *
     * @param tableName название таблицы
     * @return true если таблица существует
     */
    @SneakyThrows
    private boolean isTableExist(String tableName) {
        Statement statement = connectionFactory.getConnection().createStatement();
        String sql = String.format(CHECK_TABLE_SQL_PATTERN, tableName);
        log.info("SQL -> {}", sql);
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        boolean exists = resultSet.getBoolean("exists");
        statement.close();
        return exists;
    }

    /**
     * Создание шаблона вставки Sql в таблицу соответствующую таблицы в БД.
     *
     * @param clazz класс-таблица
     * @return строка-шаблон
     */
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
}
