package summer.config;

import summer.core.Scanner;

public interface Config {

    Class<?> getImplementation(Class<?> target);

    Scanner getClassScanner();

}
