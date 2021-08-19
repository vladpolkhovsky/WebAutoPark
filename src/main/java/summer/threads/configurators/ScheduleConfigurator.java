package summer.threads.configurators;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import summer.configurators.ProxyConfigurator;
import summer.core.Context;
import summer.threads.annotations.Schedule;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class ScheduleConfigurator implements ProxyConfigurator {

    @Override
    public <T> T makeProxy(T object, Class<T> implementation, Context context) {
        List<Method> asyncPresents = Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Schedule.class)).collect(Collectors.toList());
        asyncPresents.forEach(method -> {
            if (!method.getReturnType().equals(void.class))
                throw new RuntimeException("Method with @Schedule must return void");
            if ((method.getModifiers() & Modifier.PUBLIC & (~Modifier.STATIC) & (~Modifier.FINAL)) == 0)
                throw new RuntimeException("Method with @Schedule must be public");
        });
        if (asyncPresents.size() != 0) {
            return (T) Enhancer.create(implementation, (MethodInterceptor) this::invoke);
        }
        return object;
    }

    @SneakyThrows
    private void invoke(Object obj, MethodProxy method, Object[] args, int milliseconds, int delta) {
        //Создание потока последовательного выполнения через промежуток времени
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread invokeThread = new Thread(() -> {
                        //Создание потока выполнения с таймаутом
                        //Далее алогично @Async
                        ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                Thread fThread = Executors.defaultThreadFactory().newThread(r);
                                fThread.setDaemon(true);
                                return fThread;
                            }
                        });
                        try {
                            executorService.submit(() -> {
                                try {
                                    return method.invokeSuper(obj, args);
                                } catch (Throwable throwable) {
                                    log.warn(obj.getClass().getSuperclass().getName() + "." + method.getSuperName() + " was interrupted by timeout");
                                }
                                return null;
                            }).get(milliseconds, TimeUnit.MILLISECONDS);
                        } catch (TimeoutException exception) {
                            log.warn(obj.getClass().getSuperclass().getName() + "." + method.getSuperName() + " was interrupted by timeout");
                            executorService.shutdownNow();
                        } catch (Exception exception) {
                            log.warn("Error.", exception);
                            executorService.shutdownNow();
                        }
                        executorService.shutdown();
                    });
                    invokeThread.setDaemon(true);
                    invokeThread.start();
                    Thread.currentThread().sleep(delta);
                } catch (InterruptedException e) {
                    log.warn("Error.", e);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @SneakyThrows
    private Object invoke(Object object, Method method, Object[] args, MethodProxy methodProxy) {
        Schedule schedulesync = method.getAnnotation(Schedule.class);
        if (schedulesync != null) {
            System.out.println(method);
            Thread thread = new Thread(() -> this.invoke(object, methodProxy, args, schedulesync.timeout(), schedulesync.delta()));
            thread.setDaemon(true);
            thread.start();
            return null;
        }
        return methodProxy.invokeSuper(object, args);
    }

}
