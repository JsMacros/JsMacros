package xyz.wagyourtail.jsmacros.core.library.impl;

import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.IFConsumer;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

/**
 * Consumer implementation for wrapping consumers to match the language spec.
 * 
 * An instance of this class is passed to scripts as the {@code consumer} variable.
 * 
 * @since 1.2.5
 * 
 * @author Wagyourtail
 */
 @Library(value = "consumer", onlyAllow = ".js", perExec = true)
public class FConsumer extends BaseLibrary implements IFConsumer<Function<Object[], Object>, Function<Object[], Object>, Function<Object[], Object>> {
    
    private final LinkedBlockingQueue<Thread> tasks = new LinkedBlockingQueue<>();

    /**
     * @since 1.2.7
     * @param c
     * @custom.replaceParams c: (arg0: A, arg1?: B) => R
     * @return a new {@link MethodWrapper MethodWrapper}
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> autoWrap(Function<Object[], Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrapper<A, B, R>() {

            @Override
            public int compare(Object o1, Object o2) {
                return (int) (Object) apply(o1, o2);
            }

            @Override
            public void accept(Object arg0, Object arg1) {
                apply(arg0, arg1);
            }

            @Override
            public void accept(Object arg0) {
                apply(arg0, null);
            }

            @Override
            public R apply(Object t) {
                return this.apply(t, null);
            }

            @Override
            public R apply(Object t, Object u) {
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
                return (R) retVal;
            }

            @Override
            public boolean test(Object t) {
                return (boolean) (Object) apply(t, null);
            }

            @Override
            public void run() {
                apply(null, null);
            }

            @Override
            public boolean test(Object t, Object u) {
                return (boolean) (Object) apply(t, u);
            }

            @Override
            public R get() {
                return apply(null, null);
            }
        };
    }
    
    /**
     * @since 1.2.7
     * @param c
     * @custom.replaceParams c: (arg0: A, arg1?: B) => R
     * @return a new {@link MethodWrapper MethodWrapper}
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> autoWrapAsync(Function<Object[], Object> c) {
        Thread th = Thread.currentThread();
        return new MethodWrapper<A, B, R>() {

            @Override
            public int compare(Object o1, Object o2) {
                return (int) (Object) apply(o1, o2);
            }

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
            public R apply(Object t) {
                return apply(t, null);
            }

            @Override
            public R apply(Object t, Object u) {
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
                return (R) retVal;
            }

            @Override
            public boolean test(Object t) {
                return (boolean) (Object) apply(t, null);
            }

            @Override
            public boolean test(Object t, Object u) {
                return (boolean) (Object) apply(t, u);
            }

            @Override
            public void run() {
                accept(null, null);
            }

            @Override
            public R get() {
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
     * @custom.replaceParams c: (arg0: A, arg1?: B) => R
     * @return
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> toConsumer(Function<Object[], Object> c) {
        return autoWrap(c);
    }
    
    /**
     * Wraps a BiConsumer to match the guest language requirements.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @custom.replaceParams c: (arg0: A, arg1?: B) => R
     * @return
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> toBiConsumer(Function<Object[], Object> c) {
        return autoWrap(c);
    }
    
    /**
     * Wraps a Consumer to match the guest language requirements, without halting the thread the consumer's called in.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @custom.replaceParams c: (arg0: A, arg1?: B) => R
     * @return
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> toAsyncConsumer(Function<Object[], Object> c) {
        return autoWrapAsync(c);
    }
    
    /**
     * Wraps a BiConsumer to match the guest language requirements, without halting the thread the consumer's called in.
     * 
     * @since 1.2.5
     * @deprecated
     * @param c
     * @custom.replaceParams c: (arg0: A, arg1?: B) => R
     * @return
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> toAsyncBiConsumer(Function<Object[], Object> c) {
        return autoWrapAsync(c);
    }
}
