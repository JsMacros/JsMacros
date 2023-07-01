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
    public int hashCode() {
        return base.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseHelper) {
            return base.equals(((BaseHelper<?>) obj).base);
        }
        return base.equals(obj);
    }

}
