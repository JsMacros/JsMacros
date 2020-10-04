package xyz.wagyourtail.jsmacros.api.functions;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import xyz.wagyourtail.jsmacros.api.Functions;
import xyz.wagyourtail.jsmacros.api.MethodWrappers;

/**
 * Consumer implementation for wrapping consumers to match the language spec.
 * 
 * An instance of this class is passed to scripts as the {@code consumer} variable.
 * 
 * @since 1.2.5
 * 
 * @author Wagyourtail
 *
 */
public class FConsumer extends Functions {
    private LinkedBlockingQueue<Thread> tasks = new LinkedBlockingQueue<>();

    public FConsumer(String libName) {
        super(libName);
    }


    public FConsumer(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    /**
     * Wraps a Consumer to match the guest language requirements.
     * 
     * @since 1.2.5
     * 
     * @param c
     * @return a new {@link xyz.wagyourtail.jsmacros.api.MethodWrappers.Consumer ConsumerWrappers.Consumer}
     */
    public MethodWrappers.Consumer<Object> toConsumer(Consumer<Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrappers.Consumer<Object>() {
            
            @Override
            public void accept(Object arg0) {
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
            }
        };
    }
    
    /**
     * Wraps a BiConsumer to match the guest language requirements.
     * 
     * @since 1.2.5
     * 
     * @param c
     * @return a new {@link xyz.wagyourtail.jsmacros.api.MethodWrappers.BiConsumer ConsumerWrappers.BiConsumer}
     */
    public MethodWrappers.BiConsumer<Object, Object> toBiConsumer(BiConsumer<Object, Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrappers.BiConsumer<Object, Object>() {
            
            @Override
            public void accept(Object arg0, Object arg1) {
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
            }
        };
    }
    
    /**
     * Wraps a Consumer to match the guest language requirements, without halting the thread the consumer's called in.
     * 
     * @since 1.2.5
     * 
     * @param c
     * @return a new {@link xyz.wagyourtail.jsmacros.api.MethodWrappers.Consumer ConsumerWrappers.Consumer}
     */
    public MethodWrappers.Consumer<Object> toAsyncConsumer(Consumer<Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrappers.Consumer<Object>() {
            
            @Override
            public void accept(Object arg0) {
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
            }
        };  
    }
    
    /**
     * Wraps a BiConsumer to match the guest language requirements, without halting the thread the consumer's called in.
     * 
     * @since 1.2.5
     * 
     * @param c
     * @return a new {@link xyz.wagyourtail.jsmacros.api.MethodWrappers.BiConsumer ConsumerWrappers.BiConsumer}
     */
    public MethodWrappers.BiConsumer<Object, Object> toAsyncBiConsumer(BiConsumer<Object, Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrappers.BiConsumer<Object, Object>() {

            @Override
            public void accept(Object arg0, Object arg1) {
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
            }
        };
    }
}
