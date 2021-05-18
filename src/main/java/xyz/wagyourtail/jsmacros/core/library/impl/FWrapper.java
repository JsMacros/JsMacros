package xyz.wagyourtail.jsmacros.core.library.impl;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.ContextContainer;
import xyz.wagyourtail.jsmacros.core.language.impl.JavascriptLanguageDefinition;
import xyz.wagyourtail.jsmacros.core.library.IFWrapper;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLanguageLibrary;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;


/**
 * {@link FunctionalInterface} implementation for wrapping methods to match the language spec.
 *
 * An instance of this class is passed to scripts as the {@code JavaWrapper} variable.
 *
 * Javascript:
 * language spec requires that only one thread can hold an instance of the language at a time,
 * so this implementation uses a non-preemptive queue for the threads that call the resulting {@link MethodWrapper
 * MethodWrappers}.
 *
 * JEP:
 * language spec requires everything to be on the same thread, on the java end, so all calls to {@link MethodWrapper
 * MethodWrappers}
 * call back to JEP's starting thread and wait for the call to complete. This means that JEP can sometimes have trouble
 * closing properly, so if you use any {@link MethodWrapper MethodWrappers}, be sure to call FConsumer#stop(), to close
 * the process,
 * otherwise it's a memory leak.
 *
 * Jython:
 * no limitations
 *
 * LUA:
 * no limitations
 *
 * @author Wagyourtail
 * @since 1.2.5, re-named from {@code consumer} in 1.4.0
 */
@Library(value = "JavaWrapper", languages = JavascriptLanguageDefinition.class)
@SuppressWarnings("unused")
public class FWrapper extends PerExecLanguageLibrary<Context> implements IFWrapper<Function<Object[], Object>> {
    public final LinkedBlockingQueue<WrappedThread> tasks = new LinkedBlockingQueue<>();


    public FWrapper(ContextContainer<Context> ctx, Class<? extends BaseLanguage<Context>> language) {
        super(ctx, language);

        try {
            tasks.put(new WrappedThread(ctx.getLockThread(), true));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param c
     *
     * @return a new {@link MethodWrapper MethodWrapper}
     *
     * @since 1.3.2
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> methodToJava(Function<Object[], Object> c) {
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
                try {
                    boolean onNewThread = !Core.instance.threadContext.containsKey(Thread.currentThread());
                    if (onNewThread) {
                        tasks.put(new WrappedThread(Thread.currentThread(), true));

                        WrappedThread joinable = tasks.peek();
                        while (joinable.thread != Thread.currentThread()) {
                            joinable.waitFor();
                            joinable = tasks.peek();
                        }

                        Core.instance.threadContext.put(Thread.currentThread(), ctx.getCtx());
                        ctx.getCtx().getContext().get().enter();
                        Object retVal;
                        try {
                            retVal = c.apply(new Object[] {t, u});
                        } finally {
                            ctx.getCtx().getContext().get().leave();
                            Core.instance.threadContext.remove(Thread.currentThread());

                            tasks.poll().release();
                        }
                        return (R) retVal;
                    } else {
                        return (R) c.apply(new Object[] {t, u});
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
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
     * @param c
     *
     * @return a new {@link MethodWrapper MethodWrapper}
     *
     * @since 1.3.2
     */
    @Override
    public <A, B, R> MethodWrapper<A, B, R> methodToJavaAsync(Function<Object[], Object> c) {
        return new MethodWrapper<A, B, R>() {

            @Override
            public int compare(Object o1, Object o2) {
                return (int) (Object) apply(o1, o2);
            }

            @Override
            public void accept(Object arg0, Object arg1) {
                Thread t = new Thread(() -> {
                    try {
                        tasks.put(new WrappedThread(Thread.currentThread(), true));

                        WrappedThread joinable = tasks.peek();
                        while (joinable.thread != Thread.currentThread()) {
                            joinable.waitFor();
                            joinable = tasks.peek();
                        }

                        Core.instance.threadContext.put(Thread.currentThread(), ctx.getCtx());
                        ctx.getCtx().getContext().get().enter();
                        try {
                            c.apply(new Object[] {arg0, arg1});
                        } finally {
                            ctx.getCtx().getContext().get().leave();

                            tasks.poll().release();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
                try {
                    boolean onNewThread = !Core.instance.threadContext.containsKey(Thread.currentThread());
                    if (onNewThread) {
                        tasks.put(new WrappedThread(Thread.currentThread(), true));

                        WrappedThread joinable = tasks.peek();
                        while (joinable.thread != Thread.currentThread()) {
                            joinable.waitFor();
                            joinable = tasks.peek();
                        }

                        Core.instance.threadContext.put(Thread.currentThread(), ctx.getCtx());
                        ctx.getCtx().getContext().get().enter();
                        Object retVal;
                        try {
                            retVal = c.apply(new Object[] {t, u});
                        } finally {
                                ctx.getCtx().getContext().get().leave();
                                Core.instance.threadContext.remove(Thread.currentThread());

                                tasks.poll().release();
                        }
                        return (R) retVal;
                    } else {
                        return (R) c.apply(new Object[] {t, u});
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
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
     * JS only, puts current task at end of queue.
     * use with caution, don't accidentally cause circular waiting.
     * @throws InterruptedException
     */
    public void deferCurrentTask() throws InterruptedException {
        ctx.getCtx().getContext().get().leave();
        Core.instance.threadContext.remove(Thread.currentThread());

        tasks.poll().release();

        tasks.put(new WrappedThread(Thread.currentThread(), true));
        WrappedThread joinable = tasks.peek();
        while (joinable.thread != Thread.currentThread()) {
            joinable.waitFor();
            joinable = tasks.peek();
        }

        Core.instance.threadContext.put(Thread.currentThread(), ctx.getCtx());
        ctx.getCtx().getContext().get().enter();

    }

    /**
     * Close the current context, more important in JEP as they won't close themselves if you use other functions in
     * this class
     *
     * @since 1.2.2
     */
    @Override
    public void stop() {
        ctx.getCtx().closeContext();
    }

    public static class WrappedThread {
        public Thread thread;
        public boolean notDone;

        public WrappedThread(Thread thread, boolean notDone) {
            this.thread = thread;
            this.notDone = notDone;
        }

        public synchronized void waitFor() throws InterruptedException {
            if (this.notDone) {
                this.wait();
            }
        }

        public synchronized void release() {
            synchronized (this) {
                this.notDone = false;
                this.notifyAll();
            }
        }
    }
}
