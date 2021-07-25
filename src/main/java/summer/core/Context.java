package summer.core;

import summer.config.Config;

public interface Context {

    <T> T getObject(Class<? extends T> type);

    Config getConfig();

    void setFactory(ObjectFactory factory);

}
