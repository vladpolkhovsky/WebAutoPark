package summer.orm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Отвечает за идентефекацию типа поля в CREATE запросе
 */
@AllArgsConstructor
@Getter
public enum SqlFieldType {
    INTEGER(Integer.class, "integer"),
    LONG(Long.class, "integer"),
    STRING(String.class, "varchar(255)");

    private final Class<?> type;

    private final String sqlType;
}
