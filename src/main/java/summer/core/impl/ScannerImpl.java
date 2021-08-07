package summer.core.impl;

import org.reflections.Reflections;
import summer.core.Scanner;

import java.util.Set;

public class ScannerImpl implements Scanner {

    private Reflections reflections;

    public ScannerImpl(String packageName) {
        this.reflections = new Reflections(packageName);
    }

    @Override
    public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
        return reflections.getSubTypesOf(type);
    }

    @Override
    public Reflections getReflections() {
        return reflections;
    }
}
