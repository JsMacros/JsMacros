package xyz.wagyourtail.jsmacros.extensionbase;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IFConsumer<T> {
    
    @Deprecated
    public MethodWrapper<Object, Object> toConsumer(Consumer<Object> c);
    @Deprecated
    public MethodWrapper<Object, Object> toBiConsumer(BiConsumer<Object, Object> c);
    @Deprecated
    public MethodWrapper<Object, Object> toAsyncConsumer(Consumer<Object> c);
    @Deprecated
    public MethodWrapper<Object, Object> toAsyncBiConsumer(BiConsumer<Object, Object> c);
    
    public MethodWrapper<Object, Object> autoWrap(T c);
    public MethodWrapper<Object, Object> autoWrapAsync(T c);
    
}
