package summer.orm.impl;

import lombok.extern.slf4j.Slf4j;
import summer.core.Context;
import summer.core.annotations.Autowired;
import summer.orm.ConnectionFactory;
import summer.orm.EntityManager;
import summer.orm.service.PostgreDataBaseService;

import java.util.List;
import java.util.Optional;

/**
 * Отвечает за взаимдействие с БД.
 * Сохранения, получение объектов из БД.
 */
@Slf4j
public class EntityManagerImpl implements EntityManager {

    @Autowired
    private ConnectionFactory connection;

    @Autowired
    private PostgreDataBaseService dataBaseService;

    @Autowired
    private Context context;

    @Override
    public <T> Optional<T> get(Long id, Class<T> clazz) {
        return dataBaseService.get(id, clazz);
    }

    @Override
    public Long save(Object object) {
        return dataBaseService.save(object);
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        return dataBaseService.getAll(clazz);
    }

}
