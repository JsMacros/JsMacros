package xyz.wagyourtail.jsmacros.api.functions;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
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
public class FConsumer extends Functions implements IFConsumer<Function<Object[], Object>, Function<Object[], Object>, Function<Object[], Object>> {
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
     * @return a new {@link xyz.wagyourtail.jsmacros.extensionbase.MethodWrapper MethodWrapper}
     */
    @Override
    public MethodWrapper<Object, Object, Object> autoWrap(Function<Object[], Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrapper<Object, Object, Object>() {
            
            @Override
            public void accept(Object arg0, Object arg1) {
                apply(arg0, arg1);
            }

            @Override
            public void accept(Object arg0) {
                apply(arg0, null);
            }

            @Override
            public Object apply(Object t) {
                return this.apply(t, null);
            }

            @Override
            public Object apply(Object t, Object u) {
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
                Object retVal = c.apply(new Object[] {t, u});
                tasks.poll();
                return retVal;
            }

            @Override
            public boolean test(Object t) {
                return (boolean) apply(t, null);
            }

            @Override
            public void run() {
                apply(null, null);
            }

            @Override
            public boolean test(Object t, Object u) {
                return (boolean) apply(t, u);
            }

            @Override
            public Object get() {
                return apply(null, null);
            }
        };
    }
    
    /**
     * @since 1.2.7
     * @param c
     * @return a new {@link xyz.wagyourtail.jsmacros.extensionbase.MethodWrapper MethodWrapper}
     */
    @Override
    public MethodWrapper<Object, Object, Object> autoWrapAsync(Function<Object[], Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrapper<Object, Object, Object>() {

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
                    c.apply(new Object[] {arg0, arg1});
                    tasks.poll();
                    
                });
                t.start();
            }

            @Override
            public void accept(Object t) {
                this.accept(t, null);
            }

            @Override
            public Object apply(Object t) {
                return apply(t, null);
            }

            @Override
            public Object apply(Object t, Object u) {
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
                Object retVal = c.apply(new Object[] {t, u});
                tasks.poll();
                return retVal;
            }

            @Override
            public boolean test(Object t) {
                return (boolean) apply(t, null);
            }

            @Override
            public boolean test(Object t, Object u) {
                return (boolean) apply(t, u);
            }

            @Override
            public void run() {
                accept(null, null);
            }

            @Override
            public Object get() {
                return apply(null, null);
            }
        };
    }
    
    /**
     * Wraps a Consumer to match the guest language requirements.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @return
     */
    @Override
    public MethodWrapper<Object, Object, Object> toConsumer(Function<Object[], Object> c) {
        return autoWrap(c);
    }
    
    /**
     * Wraps a BiConsumer to match the guest language requirements.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @return
     */
    @Override
    public MethodWrapper<Object, Object, Object> toBiConsumer(Function<Object[], Object> c) {
        return autoWrap(c);
    }
    
    /**
     * Wraps a Consumer to match the guest language requirements, without halting the thread the consumer's called in.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @return
     */
    @Override
    public MethodWrapper<Object, Object, Object> toAsyncConsumer(Function<Object[], Object> c) {
        return autoWrapAsync(c);
    }
    
    /**
     * Wraps a BiConsumer to match the guest language requirements, without halting the thread the consumer's called in.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @return
     */
    @Override
    public MethodWrapper<Object, Object, Object> toAsyncBiConsumer(Function<Object[], Object> c) {
        return autoWrapAsync(c);
    }
}
