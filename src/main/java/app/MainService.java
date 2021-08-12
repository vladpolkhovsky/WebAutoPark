package app;

import app.entity.CarEntity;
import app.entity.PersonEntity;
import lombok.SneakyThrows;
import summer.core.annotations.Autowired;
import summer.core.annotations.InitMethod;
import summer.orm.EntityManager;
import summer.threads.annotations.Async;

import java.util.List;

public class MainService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    AsyncMethod asyncMethod;

    @InitMethod
    public void main() {

        asyncMethod.method();

        System.out.println("hello");

        System.out.println(asyncMethod.method2());

//        PersonEntity personEntity = PersonEntity.builder()
//                .fname(System.currentTimeMillis() + "")
//                .sname(System.currentTimeMillis() + "")
//                .age(10)
//                .build();
//        entityManager.save(personEntity);
//
//        CarEntity carEntity = CarEntity.builder()
//                .color("Black")
//                .build();
//        entityManager.save(carEntity);
//
//        List<PersonEntity> personEntities = entityManager.getAll(PersonEntity.class);
//        List<CarEntity> carEntities = entityManager.getAll(CarEntity.class);
//
//        System.out.println(carEntities);
//        System.out.println(personEntities);
//
//        System.out.println(carEntity + " " + entityManager.get(carEntity.getIdCar(), carEntity.getClass()));
    }


    public static class AsyncMethod {

        @Async(timeout = 1000 * 5)
        @SneakyThrows
        public void method() {
            Thread.sleep(7000);
            System.out.println("Мяв");
        }

        public Object method2() {
            return new Object();
        }

    }



}
