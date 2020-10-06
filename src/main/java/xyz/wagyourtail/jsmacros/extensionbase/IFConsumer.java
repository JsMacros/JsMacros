package xyz.wagyourtail.jsmacros.extensionbase;

public interface IFConsumer<T, U> {
    
    @Deprecated
    public MethodWrapper<Object, Object> toConsumer(U c);
    @Deprecated
    public MethodWrapper<Object, Object> toBiConsumer(T c);
    @Deprecated
    public MethodWrapper<Object, Object> toAsyncConsumer(U c);
    @Deprecated
    public MethodWrapper<Object, Object> toAsyncBiConsumer(T c);
    
    public MethodWrapper<Object, Object> autoWrap(T c);
    public MethodWrapper<Object, Object> autoWrapAsync(T c);
    
}
