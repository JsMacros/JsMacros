package xyz.wagyourtail.jsmacros.client.api.helpers;

public abstract class BaseHelper<T> {
    protected T base;

    public BaseHelper(T base) {
        this.base = base;
    }
    
    public T getRaw() {
        return base;
    }
}
