package app;

import app.entity.CarEntity;
import app.entity.PersonEntity;
import summer.core.annotations.Autowired;
import summer.core.annotations.InitMethod;
import summer.orm.EntityManager;

import java.util.List;

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
        entityManager.save(personEntity);

        CarEntity carEntity = CarEntity.builder()
                .color("Black")
                .build();
        entityManager.save(carEntity);

        List<PersonEntity> personEntities = entityManager.getAll(PersonEntity.class);
        List<CarEntity> carEntities = entityManager.getAll(CarEntity.class);

        System.out.println(carEntities);
        System.out.println(personEntities);

        System.out.println(carEntity + " " + entityManager.get(carEntity.getIdCar(), carEntity.getClass()));
    }

}
