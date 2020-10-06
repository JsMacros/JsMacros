package xyz.wagyourtail.jsmacros.extensionbase;

public interface IFConsumer<T, U, V> {
    
    @Deprecated
    public MethodWrapper<Object, Object, Object> toConsumer(U c);
    @Deprecated
    public MethodWrapper<Object, Object, Object> toBiConsumer(T c);
    @Deprecated
    public MethodWrapper<Object, Object, Object> toAsyncConsumer(U c);
    @Deprecated
    public MethodWrapper<Object, Object, Object> toAsyncBiConsumer(T c);
    
    public MethodWrapper<Object, Object, Object> autoWrap(V c);
    public MethodWrapper<Object, Object, Object> autoWrapAsync(V c);
    
}
