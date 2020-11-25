package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.MethodWrapper;

/**
 * Consumer implementation for wrapping consumers to match the language spec.
 *
 * An instance of this class is passed to scripts as the {@code consumer} variable.
 *
 * Javascript:
 * language spec requires that only one thread can hold an instance of the language at a time,
 * so this implementation uses a non-preemptive queue for the threads that call the resulting {@link MethodWrapper MethodWrappers}.
 *
 * JEP:
 * language spec requires everything to be on the same thread, on the java end, so all calls to {@link MethodWrapper MethodWrappers}
 * call back to JEP's starting thread and wait for the call to complete. This means that JEP can sometimes have trouble
 * closing properly, so if you use any {@link MethodWrapper MethodWrappers}, be sure to call FConsumer#stop(), to close the process,
 * otherwise it's a memory leak.
 *
 * Jython:
 * no limitations
 *
 * LUA:
 * no limitations
 *
 *
 * @since 1.2.5
 *
 * @author Wagyourtail
 */
public interface IFConsumer<T, U, V> {
    
    /**
     * Wraps a Consumer to match the guest language requirements.
     *
     * @since 1.2.5
     * @deprecated
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) => R
     * @return
     */
    @Deprecated
    public <A, B, R> MethodWrapper<A, B, R> toConsumer(U c);
    
    /**
     * Wraps a BiConsumer to match the guest language requirements.
     *
     * @since 1.2.5
     * @deprecated
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) => R
     * @return
     */
    @Deprecated
    public <A, B, R> MethodWrapper<A, B, R> toBiConsumer(T c);
    
    /**
     * Wraps a Consumer to match the guest language requirements, without halting the thread the consumer's called in.
     *
     * @since 1.2.5
     * @deprecated
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) => R
     * @return
     */
    @Deprecated
    public <A, B, R> MethodWrapper<A, B, R> toAsyncConsumer(U c);
    
    /**
     * Wraps a BiConsumer to match the guest language requirements, without halting the thread the consumer's called in.
     *
     * @since 1.2.5
     * @deprecated
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) => R
     * @return
     */
    @Deprecated
    public <A, B, R> MethodWrapper<A, B, R> toAsyncBiConsumer(T c);
    
    /**
     * @since 1.2.7
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) => R
     * @return a new {@link MethodWrapper MethodWrapper}
     */
    public <A, B, R> MethodWrapper<A, B, R> autoWrap(V c);
    
    /**
     * @since 1.2.7
     * @param c
     * @custom.replaceParams c: (arg0?: A, arg1?: B) => R
     * @return a new {@link MethodWrapper MethodWrapper}
     */
    public <A, B, R> MethodWrapper<A, B, R> autoWrapAsync(V c);
    
    /**
     * only important for JEP... stops the instance
     *
     * @since 1.2.2
     *
     */
    public default void stop() {}
}
