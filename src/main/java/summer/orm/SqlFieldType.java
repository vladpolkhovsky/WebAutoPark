package summer.orm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SqlFieldType {

    INTEGER(Integer.class, "integer"),
    LONG(Long.class, "integer"),
    STRING(String.class, "varchar(255)");

    private final Class<?> type;

    private final String sqlType;
}
