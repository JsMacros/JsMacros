package xyz.wagyourtail.jsmacros.js.library.impl;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.library.IFWrapper;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLanguageLibrary;
import xyz.wagyourtail.jsmacros.js.language.impl.GraalLanguageDefinition;
import xyz.wagyourtail.jsmacros.js.language.impl.GraalScriptContext;
import xyz.wagyourtail.jsmacros.js.language.impl.WrappedThread;

/**
 * {@link FunctionalInterface} implementation for wrapping methods to match the language spec.
 * <p>
 * An instance of this class is passed to scripts as the {@code JavaWrapper} variable.
 * <p>
 * GraalJS:
 * language spec requires that only one thread can hold an instance of the language at a time,
 * so this implementation uses a non-preemptive priority queue for the threads that call the resulting {@link MethodWrapper
 * MethodWrappers}.
 * <p>
 * JEP:
 * language spec requires everything to be on the same thread, on the java end, so all calls to {@link MethodWrapper
 * MethodWrappers}
 * call back to JEP's starting thread and wait for the call to complete. This means that JEP can sometimes have trouble
 * closing properly, so if you use any {@link MethodWrapper MethodWrappers}, be sure to call FConsumer#stop(), to close
 * the process,
 * otherwise it's a memory leak.
 * <p>
 * Jython:
 * no limitations
 * <p>
 * LUA:
 * no limitations
 *
 * @author Wagyourtail
 * @since 1.2.5, re-named from {@code consumer} in 1.4.0
 */
@Library(value = "JavaWrapper", languages = GraalLanguageDefinition.class)
@SuppressWarnings("unused")
public class FWrapper extends PerExecLanguageLibrary<Context, GraalScriptContext> implements IFWrapper<Value> {

    public FWrapper(GraalScriptContext ctx, Class<? extends BaseLanguage<Context, GraalScriptContext>> language) {
        super(ctx, language);
    }

    /**
     * @param c
     * @return a new {@link MethodWrapper MethodWrapper}
     * @since 1.3.2
     */
    @Override
    @DocletReplaceParams("c: (arg0: A, arg1: B) => R | void")
    public <A, B, R> MethodWrapper<A, B, R, GraalScriptContext> methodToJava(Value c) {
        return new JSMethodWrapper<>(c, true, 5);
    }

    /**
     * @param c
     * @return a new {@link MethodWrapper MethodWrapper}
     * @since 1.3.2
     */
    @Override
    @DocletReplaceParams("c: (arg0: A, arg1: B) => R | void")
    public <A, B, R> MethodWrapper<A, B, R, GraalScriptContext> methodToJavaAsync(Value c) {
        return new JSMethodWrapper<>(c, false, 5);
    }

    @Override
    public <A, B, R> MethodWrapper<A, B, R, ?> methodToJavaAsync(int priority, Value c) {
        return new JSMethodWrapper<>(c, false, priority);
    }

    /**
     * JS only, puts current task at end of queue.
     * use with caution, don't accidentally cause circular waiting.
     *
     * @throws InterruptedException
     */
    @Override
    public void deferCurrentTask() throws InterruptedException {
        ctx.wrapSleep(() -> {
        });
    }

    /**
     * JS/JEP only, puts current task at end of queue.
     * use with caution, don't accidentally cause circular waiting.
     *
     * @throws InterruptedException
     */
    @Override
    public void deferCurrentTask(int priorityAdjust) throws InterruptedException {
        ctx.wrapSleep(priorityAdjust, () -> {
        });
    }

    /**
     * JS/JEP only, puts current task at end of queue.
     *
     * @throws InterruptedException
     */
    @Override
    public int getCurrentPriority() {
        assert ctx.tasks.peek() != null;
        return ctx.tasks.peek().priority;
    }

    /**
     * Close the current context, more important in JEP as they won't close themselves if you use other functions in
     * this class
     *
     * @since 1.2.2
     */
    @Override
    public void stop() {
        ctx.closeContext();
    }

    private class JSMethodWrapper<T, U, R> extends MethodWrapper<T, U, R, GraalScriptContext> {
        private final Value fn;
        public final boolean await;
        public final int priority;

        JSMethodWrapper(Value fn, boolean await, int priority) {
            super(FWrapper.this.ctx);
            if (!fn.canExecute()) {
                throw new AssertionError("c is not executable");
            }
            this.fn = fn;
            this.await = await;
            this.priority = priority;
        }

        @Override
        public boolean preventSameScriptJoin() {
            return true;
        }

        private void innerAccept(Object... args) {
            if (await) {
                innerApply(args);
                return;
            }

            if (ctx.isContextClosed()) {
                throw new BaseScriptContext.ScriptAssertionError("Context closed");
            }

            Core.getInstance().threadPool.runTask(() -> {
                try {
                    WrappedThread wt = new WrappedThread(Thread.currentThread(), priority);
                    ctx.tasks.add(wt);
                    ctx.bindThread(Thread.currentThread());

                    while (true) {
                        synchronized (ctx.tasks) {
                            assert ctx.tasks.peek() != null;
                            if (ctx.tasks.peek().thread == Thread.currentThread()) break;
                        }
                        wt.waitUntilReady();
                        break;
                    }

                    if (ctx.isContextClosed()) {
                        ctx.unbindThread(Thread.currentThread());
                        assert ctx.tasks.peek() != null;
                        ctx.tasks.poll();
                        WrappedThread next = ctx.tasks.peek();
                        if (next != null) {
                            next.notifyReady();
                        }
                        throw new BaseScriptContext.ScriptAssertionError("Context closed");
                    }

                    ctx.getContext().enter();
                    try {
                        fn.executeVoid(args);
                    } catch (Throwable ex) {
                        Core.getInstance().profile.logError(ex);
                    } finally {
                        ctx.getContext().leave();
                        ctx.releaseBoundEventIfPresent(Thread.currentThread());
                        Core.getInstance().profile.joinedThreadStack.remove(Thread.currentThread());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    ctx.unbindThread(Thread.currentThread());
                    ctx.tasks.poll();
                    WrappedThread next = ctx.tasks.peek();
                    if (next != null) {
                        next.notifyReady();
                    }
                }
            });
        }

        private <R2> R2 innerApply(Object... args) {
            if (ctx.isContextClosed()) {
                throw new BaseScriptContext.ScriptAssertionError("Context closed");
            }

            if (ctx.getBoundThreads().contains(Thread.currentThread())) {
                return (R2) fn.execute(args).as(Object.class);
            }

            try {
                ctx.bindThread(Thread.currentThread());
                ctx.tasks.add(new WrappedThread(Thread.currentThread(), priority));

                WrappedThread wt = new WrappedThread(Thread.currentThread(), priority);
                ctx.tasks.add(wt);
                ctx.bindThread(Thread.currentThread());

                while (true) {
                    synchronized (ctx.tasks) {
                        assert ctx.tasks.peek() != null;
                        if (ctx.tasks.peek().thread == Thread.currentThread()) break;
                    }
                    wt.waitUntilReady();
                    break;
                }

                if (ctx.isContextClosed()) {
                    ctx.unbindThread(Thread.currentThread());
                    ctx.tasks.poll();
                    WrappedThread next = ctx.tasks.peek();
                    if (next != null) {
                        next.notifyReady();
                    }
                    throw new BaseScriptContext.ScriptAssertionError("Context closed");
                }

                ctx.getContext().enter();
                try {
                    if (await && Core.getInstance().profile.checkJoinedThreadStack()) {
                        Core.getInstance().profile.joinedThreadStack.add(Thread.currentThread());
                    }
                    return (R2) fn.execute(args).as(Object.class);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                } finally {
                    ctx.getContext().leave();
                    ctx.releaseBoundEventIfPresent(Thread.currentThread());
                    Core.getInstance().profile.joinedThreadStack.remove(Thread.currentThread());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                ctx.unbindThread(Thread.currentThread());
                ctx.tasks.poll();
                WrappedThread next = ctx.tasks.peek();
                if (next != null) {
                    next.notifyReady();
                }
            }
        }

        @Override
        public void accept(T t) {
            innerAccept(t);
        }

        @Override
        public void accept(T t, U u) {
            innerAccept(t, u);
        }

        @Override
        public R apply(T t) {
            return innerApply(t);
        }

        @Override
        public R apply(T t, U u) {
            return innerApply(t, u);
        }

        @Override
        public boolean test(T t) {
            return innerApply(t);
        }

        @Override
        public boolean test(T t, U u) {
            return innerApply(t, u);
        }

        @Override
        public void run() {
            innerAccept();
        }

        @Override
        public int compare(T o1, T o2) {
            return innerApply(o1, o2);
        }

        @Override
        public R get() {
            return innerApply();
        }

    }

}
