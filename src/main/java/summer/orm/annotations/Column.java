package summer.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Название колонки в БД
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
    boolean nullable() default true;
    boolean unique() default false;
}
