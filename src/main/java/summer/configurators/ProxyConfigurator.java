package summer.configurators;

public interface ProxyConfigurator {

    <T> T makeProxy(T object, Class<T> implementation);

}
