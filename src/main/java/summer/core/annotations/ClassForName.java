package summer.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Устанавливает в полу объект заданного класса(по имени)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassForName {
    String value() default "";
}
