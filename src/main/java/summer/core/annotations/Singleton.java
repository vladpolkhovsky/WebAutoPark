package summer.core.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Отвечает за сохранение объекта в кэше после создания
 */
@Retention(RUNTIME)
public @interface Singleton {
}
