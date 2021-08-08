package summer.orm.impl;

import lombok.extern.slf4j.Slf4j;
import summer.core.Context;
import summer.core.annotations.Autowired;
import summer.core.annotations.InitMethod;
import summer.orm.ConnectionFactory;
import summer.orm.EntityManager;
import summer.orm.annotations.Table;
import summer.orm.service.PostgreDataBaseService;

import java.util.List;
import java.util.Set;

@Slf4j
public class EntityManagerImpl implements EntityManager {

    @Autowired
    private ConnectionFactory connection;

    @Autowired
    private PostgreDataBaseService dataBaseService;

    @Autowired
    private Context context;

    @InitMethod
    public void init() {
        Set<Class<?>> typesAnnotatedWithTable = context.getConfig()
                .getScanner().getReflections().getTypesAnnotatedWith(Table.class);
        for (Class<?> type : typesAnnotatedWithTable) {
            boolean tableExists = dataBaseService.isTableExist(type.getAnnotation(Table.class).name());
            log.info("Class {} annotated with @Table. Table exists -> {}",
                    type.getName(), tableExists);
            if (!tableExists)
                dataBaseService.createTable(type);
        }
    }

    @Override
    public <T> T get(Long id, Class<T> clazz) {
        return null;
    }

    @Override
    public Long save(Object object) {
        return dataBaseService.save(object);
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        return null;
    }

}
