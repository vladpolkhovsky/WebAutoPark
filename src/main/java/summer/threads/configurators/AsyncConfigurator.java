package summer.threads.configurators;

import lombok.SneakyThrows;
import net.sf.cglib.proxy.*;
import summer.configurators.ProxyConfigurator;
import summer.core.Context;
import summer.threads.annotations.Async;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class AsyncConfigurator implements ProxyConfigurator {

    @Override
    public <T> T makeProxy(T object, Class<T> implementation, Context context) {
        List<Method> asyncPresents = Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Async.class)).collect(Collectors.toList());
        asyncPresents.forEach(method -> {
           if (!method.getReturnType().equals(void.class))
               throw new RuntimeException("Method with @Async must return void");
           if ((method.getModifiers() & Modifier.PUBLIC & (~Modifier.STATIC) & (~Modifier.FINAL)) == 0)
               throw new RuntimeException("Method with @Async must be public");
        });
        if (asyncPresents.size() != 0) {
            return (T)Enhancer.create(implementation, (MethodInterceptor) this::invoke);
        }
        return object;
    }

    @SneakyThrows
    private void invoke(Object obj, MethodProxy method, Object[] args, int milliseconds) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.submit(() -> {
                try {
                    return method.invokeSuper(obj, args);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return null;
            }).get(milliseconds, TimeUnit.MILLISECONDS);
        } catch (TimeoutException exception) {
            System.out.println(obj.getClass().getSuperclass() + "." + method.getSuperName() + " was interrupted by timeout");
            executorService.shutdownNow();
        }
    }

    @SneakyThrows
    private Object invoke(Object object, Method method, Object[] args, MethodProxy methodProxy) {
        Async async = method.getAnnotation(Async.class);
        if (async != null) {
            new Thread(() -> this.invoke(object, methodProxy, args, async.timeout())).start();
            return null;
        }
        return methodProxy.invokeSuper(object, args);
    }

}
