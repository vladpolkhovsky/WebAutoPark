package summer.orm.impl;

import summer.core.annotations.Autowired;
import summer.orm.ConnectionFactory;
import summer.orm.EntityManager;

import java.util.List;

public class EntityManagerImpl implements EntityManager {

    @Autowired
    private ConnectionFactory connection;

    @Override
    public <T> T get(Long id, Class<T> clazz) {
        return null;
    }

    @Override
    public void save(Object object) {

    }

    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        return null;
    }

}
