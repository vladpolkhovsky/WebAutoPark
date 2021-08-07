package summer.orm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Используется для определения первичного ключа
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ID {
    String name() default "";
}
