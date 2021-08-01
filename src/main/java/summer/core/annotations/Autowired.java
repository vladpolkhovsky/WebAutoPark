package summer.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Служит для индетификации полей, которые должны быть заполнены автоматически
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

}
