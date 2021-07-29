package app;

import summer.Application;
import summer.config.ConfigurationClass;
import summer.core.Context;
import summer.core.annotations.Autowired;
import summer.core.annotations.ClassForName;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Evgeny Borisov
 */
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

        @Override
        public void voice() {
            System.out.println("не бип, а кря");
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

    public static class SimpleList extends AbstractList<Movable> {

        private List<Movable> list = new ArrayList<>();

        @Override
        public boolean add(Movable movable) {
            return list.add(movable);
        }

        @Override
        public Movable get(int index) {
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }
    }

    public static class Trip {

        @ClassForName("app.Main$SimpleList")
        List<Movable> transports;

        public Trip() {

        }

        void addTransport(Movable transport) {
            transports.add(transport);
        }

        void start() {
            for (int i = 0; i < transports.size(); i++) {
                transports.get(i).move();
            }
        }

    }

    public static void main(String[] args) {
        System.out.println(SimpleList.class.getName());
        Context context = Application.createContextFromConfigs(Configuration.class);
        Trip trip = context.getObject(Trip.class);
        trip.addTransport(context.getObject(Plane.class));
        trip.addTransport(context.getObject(Car.class));
        trip.start();
    }
}
