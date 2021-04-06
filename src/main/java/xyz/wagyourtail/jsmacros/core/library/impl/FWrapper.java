package xyz.wagyourtail.jsmacros.core.library.impl;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.ContextContainer;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;
import xyz.wagyourtail.jsmacros.core.language.impl.JavascriptLanguageDefinition;
import xyz.wagyourtail.jsmacros.core.library.IFWrapper;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLanguageLibrary;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;


@Library(value = "JavaWrapper", languages = JavascriptLanguageDefinition.class)
 @SuppressWarnings("unused")
public class FWrapper extends PerExecLanguageLibrary implements IFWrapper<Function<Object[], Object>> {
    
    private final LinkedBlockingQueue<Thread> tasks = new LinkedBlockingQueue<>();
    
    public FWrapper(ContextContainer<Context> ctx, Class<? extends BaseLanguage<?>> language) {
        super(ctx, language);
    }
    
    /**
     * @since 1.3.2
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) =&gt; R
     * @return a new {@link MethodWrapper MethodWrapper}
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> methodToJava(Function<Object[], Object> c) {
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
    
    /**
     * @since 1.3.2
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) =&gt; R
     * @return a new {@link MethodWrapper MethodWrapper}
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> methodToJavaAsync(Function<Object[], Object> c) {
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
    
    /**
     * Close the current context, more important in JEP as they won't close themselves if you use other functions in
     * this class
     *
     * @since 1.2.2
     *
     */
    @Override
    public void stop() {
        ctx.getCtx().closeContext();
    }
    
}
