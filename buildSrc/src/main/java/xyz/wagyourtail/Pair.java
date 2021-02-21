package xyz.wagyourtail;

public class Pair<T, U> {
    T key;
    U value;
    
    public Pair(T key, U value) {
        this.key = key;
        this.value = value;
    }
    
    public T getKey() {
        return key;
    }
    
    public U getValue() {
        return value;
    }
    
    public void setKey(T key) {
        this.key = key;
    }
    
    public void setValue(U value) {
        this.value = value;
    }
}
