package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

/**
 * {@link FunctionalInterface} implementation for wrapping methods to match the language spec.
 * <br><br>
 * An instance of this class is passed to scripts as the {@code consumer} variable.
 * <br><br>
 * Javascript:
 * language spec requires that only one thread can hold an instance of the language at a time,
 * so this implementation uses a non-preemptive priority queue for the threads that call the resulting {@link MethodWrapper MethodWrappers}.
 * <br><br>
 * JEP:
 * language spec requires everything to be on the same thread, on the java end, so all calls to {@link MethodWrapper MethodWrappers}
 * call back to JEP's starting thread and wait for the call to complete.
 * <br><br>
 * Jython:
 * no limitations
 * <br><br>
 * LUA:
 * no limitations
 *
 * @author Wagyourtail
 * @since 1.2.5, re-named from {@code consumer} in 1.3.2
 */
@Library("JavaWrapper")
public interface IFWrapper<T> {

    /**
     * @param c
     * @return a new {@link MethodWrapper MethodWrapper}
     * @since 1.4.0
     */
    @DocletReplaceParams("c: (arg0: A, arg1: B) => R | void")
    <A, B, R> MethodWrapper<A, B, R, ?> methodToJava(T c);

    /**
     * @param c
     * @return a new {@link MethodWrapper MethodWrapper}
     * @since 1.4.0
     */
    @DocletReplaceParams("c: (arg0: A, arg1: B) => R | void")
    <A, B, R> MethodWrapper<A, B, R, ?> methodToJavaAsync(T c);

    /**
     * JS/JEP ONLY
     * allows you to set the position of the thread in the queue. you can use this for return value one's too...
     *
     * @param priority
     * @param c
     * @param <A>
     * @param <B>
     * @param <R>
     * @return
     * @since 1.8.0
     */
    @DocletReplaceParams("priority: int, c: (arg0: A, arg1: B) => R | void")
    default <A, B, R> MethodWrapper<A, B, R, ?> methodToJavaAsync(int priority, T c) {
        return methodToJavaAsync(c);
    }

    /**
     * JS/JEP only, puts current task at end of queue.
     * use with caution, don't accidentally cause circular waiting.
     *
     * @throws InterruptedException
     * @since 1.4.0 [citation needed]
     */
    default void deferCurrentTask() throws InterruptedException {
        throw new AssertionError("deferCurrentTask() is not implemented for this language");
    }

    /**
     * JS/JEP only, puts current task at end of queue.
     * use with caution, don't accidentally cause circular waiting.
     *
     * @param priorityAdjust the amount to adjust the priority by
     * @throws InterruptedException
     * @since 1.8.0
     */
    default void deferCurrentTask(int priorityAdjust) throws InterruptedException {
        throw new AssertionError("deferCurrentTask() is not implemented for this language");
    }

    /**
     * JS/JEP only, get priority of current task.
     *
     * @throws InterruptedException
     * @since 1.8.0
     */
    default int getCurrentPriority() {
        throw new AssertionError("getCurrentPriority() is not implemented for this language");
    }

    /**
     * Close the current context
     *
     * @since 1.2.2
     */
    void stop();

}
