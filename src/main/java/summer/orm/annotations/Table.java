package summer.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Название таблицы в БД
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name();
}
