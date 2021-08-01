package app;

import summer.Application;
import summer.config.ConfigurationClass;
import summer.core.Context;
import summer.core.annotations.Autowired;
import summer.core.annotations.Property;

import java.util.Map;

public class Main {

    public static class Configuration implements ConfigurationClass {
        @Override
        public void addConfiguration(Map<Class<?>, Class<?>> configuration) {
            configuration.put(Movable.class, Car.class);
            configuration.put(Klaxon.class, NotDefaultKlaxon.class);
        }
    }

    public static class DefaultKlaxon implements Klaxon {
        @Override
        public void voice() {
            System.out.println("Бип");
        }
    }

    public static class NotDefaultKlaxon implements Klaxon {

        @Property("NotDefaultKlaxonSound")
        String sound;

        @Override
        public void voice() {
            System.out.println(sound);
        }

    }

    public static class Car implements Movable {

        @Autowired
        Klaxon klaxon;

        @Override
        public void move() {
            for (int i = 0; i < 5; i++)
                klaxon.voice();
            System.out.println("Еду");
        }

    }

    public static class Plane implements Movable {

        @Override
        public void move() {
            System.out.println("Лечу");
        }

    }

    public static class Trip {

        @Autowired
        Movable transport;

        void start() {
            transport.move();
        }

    }

    public static void main(String[] args) {
        Context context = Application.createContextFromConfigs(Configuration.class);
        Trip trip = context.getObject(Trip.class);
        trip.start();
    }
}
