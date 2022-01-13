package xyz.wagyourtail.jsmacros.client.api.classes.filter;

public class AllStringFilter<T> extends AnyStringFilter<T> {

    public AllStringFilter(String filterName) {
        super(filterName);
    }

    @Override
    public Boolean apply(T t) {
        String toTest = t.toString();
        return filterObjects.parallelStream().allMatch(s -> filter.compare(toTest, s));
    }
    
}
