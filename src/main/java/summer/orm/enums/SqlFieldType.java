package summer.orm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Отвечает за идентефекацию типа поля в CREATE запросе
 */
@AllArgsConstructor
@Getter
public enum SqlFieldType {
    INTEGER(Integer.class, "integer", "%s"),
    LONG(Long.class, "integer", "%s"),
    STRING(String.class, "varchar(255)", "'%s'");

    private final Class<?> type;

    private final String sqlType;
    
    private final String insertPattern;
}
