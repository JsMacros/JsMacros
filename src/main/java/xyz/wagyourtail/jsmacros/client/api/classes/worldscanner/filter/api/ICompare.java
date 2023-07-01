package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.api;

@FunctionalInterface
public interface ICompare<T> {

    boolean compare(T obj1, T obj2);

}
