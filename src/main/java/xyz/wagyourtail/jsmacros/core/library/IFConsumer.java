package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.MethodWrapper;

public interface IFConsumer<T, U, V> {
    
    @Deprecated
    public <A, B, R> MethodWrapper<A, B, R> toConsumer(U c);
    @Deprecated
    public <A, B, R> MethodWrapper<A, B, R> toBiConsumer(T c);
    @Deprecated
    public <A, B, R> MethodWrapper<A, B, R> toAsyncConsumer(U c);
    @Deprecated
    public <A, B, R> MethodWrapper<A, B, R> toAsyncBiConsumer(T c);
    
    public <A, B, R> MethodWrapper<A, B, R> autoWrap(V c);
    public <A, B, R> MethodWrapper<A, B, R> autoWrapAsync(V c);
    
}
