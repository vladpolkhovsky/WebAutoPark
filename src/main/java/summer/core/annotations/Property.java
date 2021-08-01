package summer.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Отвечает за вставку значения из файла свойств по имени
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
    String value() default "";
}
