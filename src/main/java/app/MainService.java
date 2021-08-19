package app;

import lombok.SneakyThrows;
import summer.core.annotations.Autowired;
import summer.core.annotations.InitMethod;
import summer.threads.annotations.Schedule;

public class MainService {

//    @Autowired
//    EntityManager entityManager;

    @Autowired
    AsyncMethod asyncMethod;

    @InitMethod
    @SneakyThrows
    public void main() {

        asyncMethod.method3();
        asyncMethod.method();

        Thread.sleep(3000);

        System.out.println("Begin");

        Thread.sleep(10000);

        System.out.println("End");

//        PersonEntity personEntity = PersonEntity.builder()
//                .fname(System.currentTimeMillis() + "")
//                .sname(System.currentTimeMillis() + "")
//                .age(10)
//                .date(new Date(System.currentTimeMillis()))
//                .build();
//
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

        @SneakyThrows
        public void method() {
            Thread.sleep(7000);
            System.out.println("Мяв");
        }

        public Object method2() {
            return new Object();
        }

        private static int callId = 0;

        @Schedule(delta = 100, timeout = 50000)
        public void method3() {
            System.out.println("Call with id=" + callId++);
        }

    }



}
