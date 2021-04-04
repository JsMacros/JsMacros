package xyz.wagyourtail.jsmacros.core.library.impl;

import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;
import xyz.wagyourtail.jsmacros.core.language.impl.JavascriptLanguageDefinition;
import xyz.wagyourtail.jsmacros.core.library.IFConsumer;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLanguageLibrary;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;


 @Library(value = "consumer", languages = JavascriptLanguageDefinition.class)
 @SuppressWarnings("unused")
public class FConsumer extends PerExecLanguageLibrary implements IFConsumer<Function<Object[], Object>, Function<Object[], Object>, Function<Object[], Object>> {
    
    private final LinkedBlockingQueue<Thread> tasks = new LinkedBlockingQueue<>();
    
    public FConsumer(Class<? extends BaseLanguage<?>> language, Object context, Thread thread) {
        super(language, context, thread);
    }
    
    @Override
    public <A, B, R> MethodWrapper<A, B, R> autoWrap(Function<Object[], Object> c) {
        Thread th = Thread.currentThread();
        ScriptContext<?> ctx = Core.instance.threadContext.get(th);
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
                        } catch (InterruptedException ignored) {}
                    }
                    tasks.add(Thread.currentThread());
                    Thread joinable;
                    while ((joinable = tasks.peek()) != Thread.currentThread()) {
                        try {
                            joinable.join();
                        } catch (InterruptedException ignored) {}
                    }
                }
                Core.instance.threadContext.put(Thread.currentThread(), ctx);
                Object retVal = null;
                try {
                    c.apply(new Object[] {t, u});
                } finally {
                    Core.instance.threadContext.remove(Thread.currentThread());
                    tasks.poll();
                }
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
            
            @Override
            public boolean preventSameThreadJoin() {
                return true;
            }
        };
    }
    
    @Override
    public <A, B, R> MethodWrapper<A, B, R> autoWrapAsync(Function<Object[], Object> c) {
        Thread th = Thread.currentThread();
        ScriptContext<?> ctx = Core.instance.threadContext.get(th);
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
                        } catch (InterruptedException ignored) {}
                    }
                    tasks.add(Thread.currentThread());
                    Thread joinable;
                    while ((joinable = tasks.peek()) != Thread.currentThread()) {
                        try {
                            assert joinable != null;
                            joinable.join();
                        } catch (InterruptedException ignored) {}
                    }
                    Core.instance.threadContext.put(Thread.currentThread(), ctx);
                    try {
                        c.apply(new Object[] {arg0, arg1});
                    } finally {
                        tasks.poll();
                    }
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
                        } catch (InterruptedException ignored) {}
                    }
                    tasks.add(Thread.currentThread());
                    Thread joinable;
                    while ((joinable = tasks.peek()) != Thread.currentThread()) {
                        try {
                            joinable.join();
                        } catch (InterruptedException ignored) {}
                    }
                }
                Core.instance.threadContext.put(Thread.currentThread(), ctx);
                Object retVal = null;
                try {
                    c.apply(new Object[] {t, u});
                } finally {
                    Core.instance.threadContext.remove(Thread.currentThread());
                    tasks.poll();
                }
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
    
    @Override
    public <A, B, R> MethodWrapper<A, B, R> toConsumer(Function<Object[], Object> c) {
        return autoWrap(c);
    }
    
    @Override
    public <A, B, R> MethodWrapper<A, B, R> toBiConsumer(Function<Object[], Object> c) {
        return autoWrap(c);
    }
    
    @Override
    public <A, B, R> MethodWrapper<A, B, R> toAsyncConsumer(Function<Object[], Object> c) {
        return autoWrapAsync(c);
    }
    
    @Override
    public <A, B, R> MethodWrapper<A, B, R> toAsyncBiConsumer(Function<Object[], Object> c) {
        return autoWrapAsync(c);
    }
}
