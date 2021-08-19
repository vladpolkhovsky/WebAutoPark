package summer.threads.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Метод отвечает за регулярный вызов функции с разницей в delta ms.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {
    int delta();
    int timeout() default Integer.MAX_VALUE;
}
