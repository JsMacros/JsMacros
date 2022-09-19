package xyz.wagyourtail.jsmacros.core.helpers;

public abstract class BaseHelper<T> {
    protected T base;

    public BaseHelper(T base) {
        this.base = base;
    }
    
    public T getRaw() {
        return base;
    }

    @Override
    public boolean equals(Object obj) {
        return base.equals(obj);
    }
    
}
