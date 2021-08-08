package app;

import app.entity.PersonEntity;
import summer.core.annotations.Autowired;
import summer.core.annotations.InitMethod;
import summer.orm.EntityManager;

public class MainService {

    @Autowired
    EntityManager entityManager;

    @InitMethod
    public void main() {
        PersonEntity personEntity = PersonEntity.builder()
                .fname(System.currentTimeMillis() + "")
                .sname(System.currentTimeMillis() + "")
                .age(10)
                .build();
        Long id = entityManager.save(personEntity);
        System.out.println(personEntity);
    }

}
