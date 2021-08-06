package app;

import summer.Application;

public class Main {

    public static void main(String[] args) {
        Application.createContext().getObject(MainService.class);
    }

}
