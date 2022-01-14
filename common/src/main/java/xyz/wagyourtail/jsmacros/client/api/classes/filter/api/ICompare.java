package xyz.wagyourtail.jsmacros.client.api.classes.filter.api;

@FunctionalInterface
public interface ICompare<T> {
    
    boolean compare(T obj1, T obj2);
    
}
