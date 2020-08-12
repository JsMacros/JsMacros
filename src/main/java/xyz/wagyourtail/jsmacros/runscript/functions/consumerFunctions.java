package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class consumerFunctions extends Functions {
    private LinkedBlockingQueue<Thread> tasks = new LinkedBlockingQueue<>();

    public consumerFunctions(String libName) {
        super(libName);
    }


    public consumerFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    public Consumer<Object> toConsumer(Consumer<Object> c) {
        Thread th = Thread.currentThread();
        return new Consumer<Object>() {
            @Override
            public void accept(Object arg0) {
                if (th != Thread.currentThread()) {
                    while(th.isAlive());
                    tasks.add(Thread.currentThread());
                    Thread joinable;
                    while ((joinable = tasks.peek()) != Thread.currentThread()) {
                        try {
                            joinable.join();
                        } catch (Exception e) {}
                    }
                }
                c.accept(arg0);
                tasks.poll();
            }
        };
    }
    
    public BiConsumer<Object, Object> toBiConsumer(BiConsumer<Object, Object> c) {
        Thread th = Thread.currentThread();
        return new BiConsumer<Object, Object>() {
            @Override
            public void accept(Object arg0, Object arg1) {
                if (th != Thread.currentThread()) {
                    while(th.isAlive());
                    tasks.add(Thread.currentThread());
                    Thread joinable;
                    while ((joinable = tasks.peek()) != Thread.currentThread()) {
                        try {
                            joinable.join();
                        } catch (Exception e) {}
                    }
                }
                c.accept(arg0, arg1);
                tasks.poll();
            }
        };
    }
}
