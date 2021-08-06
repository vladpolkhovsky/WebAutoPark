package app;

import summer.core.annotations.Autowired;
import summer.core.annotations.InitMethod;
import summer.orm.EntityManager;

public class MainService {

    @Autowired
    EntityManager entityManager;

    @InitMethod
    public void main() {
        System.out.println(entityManager);
    }

}
