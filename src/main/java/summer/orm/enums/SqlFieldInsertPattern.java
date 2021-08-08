package summer.orm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SqlFieldInsertPattern {
    INTEGER(Integer.class, "%s"),
    LONG(Long.class, "%s"),
    STRING(String.class, "'%s'");

    private final Class<?> type;

    private final String sqlType;
}
