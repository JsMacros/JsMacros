package xyz.wagyourtail.jsmacros.api.functions;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import xyz.wagyourtail.jsmacros.extensionbase.Functions;
import xyz.wagyourtail.jsmacros.extensionbase.IFConsumer;
import xyz.wagyourtail.jsmacros.extensionbase.MethodWrapper;

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
public class FConsumer extends Functions implements IFConsumer<BiConsumer<Object, Object>, Consumer<Object>, Function<Object[], Object>> {
    private LinkedBlockingQueue<Thread> tasks = new LinkedBlockingQueue<>();

    public FConsumer(String libName) {
        super(libName);
    }


    public FConsumer(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    /**
     * @since 1.2.7
     * @param c
     * @return
     */
    public MethodWrapper<Object, Object> autoWrap(Function<Object[], Object> c) {
        return toBiConsumer((a, b) -> c.apply(new Object[] {a,b}));
    }
    
    /**
     * @since 1.2.7
     * @param c
     * @return
     */
    public MethodWrapper<Object, Object> autoWrapAsync(Function<Object[], Object> c) {
        return toAsyncBiConsumer((a, b) -> c.apply(new Object[] {a, b}));
    }
    
    /**
     * Wraps a Consumer to match the guest language requirements.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @return a new {@link xyz.wagyourtail.jsmacros.extensionbase.MethodWrappers.Consumer ConsumerWrappers.Consumer}
     */
    public MethodWrapper<Object, Object> toConsumer(Consumer<Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrapper<Object, Object>() {
            
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

            @Override
            public void accept(Object arg0, Object arg1) {
                this.accept(arg0);
            }
        };
    }
    
    /**
     * Wraps a BiConsumer to match the guest language requirements.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @return a new {@link xyz.wagyourtail.jsmacros.extensionbase.MethodWrappers.BiConsumer ConsumerWrappers.BiConsumer}
     */
    public MethodWrapper<Object, Object> toBiConsumer(BiConsumer<Object, Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrapper<Object, Object>() {
            
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

            @Override
            public void accept(Object arg0) {
                accept(arg0, null);
            }
        };
    }
    
    /**
     * Wraps a Consumer to match the guest language requirements, without halting the thread the consumer's called in.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @return a new {@link xyz.wagyourtail.jsmacros.extensionbase.MethodWrappers.Consumer ConsumerWrappers.Consumer}
     */
    public MethodWrapper<Object, Object> toAsyncConsumer(Consumer<Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrapper<Object, Object>() {
            
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

            @Override
            public void accept(Object arg0, Object arg1) {
                this.accept(arg0);
            }
        };  
    }
    
    /**
     * Wraps a BiConsumer to match the guest language requirements, without halting the thread the consumer's called in.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @return a new {@link xyz.wagyourtail.jsmacros.extensionbase.MethodWrappers.BiConsumer ConsumerWrappers.BiConsumer}
     */
    public MethodWrapper<Object, Object> toAsyncBiConsumer(BiConsumer<Object, Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrapper<Object, Object>() {

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

            @Override
            public void accept(Object t) {
                this.accept(t, null);
            }
        };
    }
}
