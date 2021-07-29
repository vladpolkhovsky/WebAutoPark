package summer.configurators;

import summer.core.Context;

public interface ProxyConfigurator {

    <T> T makeProxy(T object, Class<T> implementation, Context context);

}
