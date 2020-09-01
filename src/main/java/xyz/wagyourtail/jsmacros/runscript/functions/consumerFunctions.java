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
        return (arg0) -> {
            if (th != Thread.currentThread()) {
                while(th.isAlive()) {
                    try {
                        th.join();
                    } catch (InterruptedException e1) {}
                }
                tasks.add(Thread.currentThread());
                Thread joinable;
                while ((joinable = tasks.peek()) != Thread.currentThread()) {
                    try {
                        joinable.join();
                    } catch (InterruptedException e1) {}
                }
            }
            c.accept(arg0);
            tasks.poll();
        };
    }
    
    public BiConsumer<Object, Object> toBiConsumer(BiConsumer<Object, Object> c) {
        Thread th = Thread.currentThread();
        return (arg0, arg1) -> {
            if (th != Thread.currentThread()) {
                while(th.isAlive()) {
                    try {
                        th.join();
                    } catch (InterruptedException e1) {}
                }
                tasks.add(Thread.currentThread());
                Thread joinable;
                while ((joinable = tasks.peek()) != Thread.currentThread()) {
                    try {
                        joinable.join();
                    } catch (InterruptedException e1) {}
                }
            }
            c.accept(arg0, arg1);
            tasks.poll();
        };
    }
    
    public Consumer<Object> toAsyncConsumer(Consumer<Object> c) {
        Thread th = Thread.currentThread();
        return (arg0) -> {
            Thread t = new Thread(() -> {
                while(th.isAlive()) {
                    try {
                        th.join();
                    } catch (InterruptedException e1) {}
                }
                tasks.add(Thread.currentThread());
                Thread joinable;
                while ((joinable = tasks.peek()) != Thread.currentThread()) {
                    try {
                        joinable.join();
                    } catch (InterruptedException e1) {}
                }
                c.accept(arg0);
                tasks.poll();
                
            });
            t.start();
            
        };
    }
    
    public BiConsumer<Object, Object> toAsyncBiConsumer(BiConsumer<Object, Object> c) {
        Thread th = Thread.currentThread();
        return (arg0, arg1) -> {
            Thread t = new Thread(() -> {
                while(th.isAlive()) {
                    try {
                        th.join();
                    } catch (InterruptedException e1) {}
                }
                tasks.add(Thread.currentThread());
                Thread joinable;
                while ((joinable = tasks.peek()) != Thread.currentThread()) {
                    try {
                        joinable.join();
                    } catch (InterruptedException e1) {}
                }
                c.accept(arg0, arg1);
                tasks.poll();
                
            });
            t.start();
        };
    }
}
