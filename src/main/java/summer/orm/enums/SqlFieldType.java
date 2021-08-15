package summer.orm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;

/**
 * Отвечает за идентефекацию типа поля в запросах.
 */
@AllArgsConstructor
@Getter
public enum SqlFieldType {
    INTEGER(Integer.class, "integer", "%s"),
    LONG(Long.class, "integer", "%s"),
    STRING(String.class, "varchar(255)", "'%s'"),
    DATE(Date.class, "date", "'%s'");

    private final Class<?> type;

    private final String sqlType;
    
    private final String insertPattern;
}
