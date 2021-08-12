package summer.threads.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *  Цепляется на метод, который обязан быть асинхронным.
 *  Важно что метод должен быть public.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {
    int timeout() default Integer.MAX_VALUE;
}
