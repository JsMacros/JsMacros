package xyz.wagyourtail.jsmacros.client.api.classes.filter;

import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IAdvancedFilter;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Etheradon
 * @since 1.6.4
 */
public class AnyStringFilter<T> implements IFilter<T>, IAdvancedFilter<T> {

    public final Set<String> filterObjects;

    protected final Compare<String> filter;
    
    public AnyStringFilter(String filterName) {
        this.filterObjects = new HashSet<>();
        filter = FilterMethod.valueOf(filterName).getMethod();
    }
    
    public AnyStringFilter<T> addOption(String... toAdd) {
        filterObjects.addAll(List.of(toAdd));
        return this;
    }

    public AnyStringFilter<T> removeOption(String... toRemove) {
        List.of(toRemove).forEach(filterObjects::remove);
        return this;
    }
    
    @Override
    public Boolean apply(T t) {
        String toTest = t.toString();
        return filterObjects.parallelStream().anyMatch(s -> filter.compare(toTest, s));
    }

    @Override
    public IFilter<T> and(IFilter<T> filter) {
        return new AndFilter<>(this, filter);
    }

    @Override
    public IFilter<T> or(IFilter<T> filter) {
        return new OrFilter<>(this, filter);
    }

    @Override
    public IFilter<T> not() {
        return new NotFilter<>(this);
    }

    public enum FilterMethod {
        CONTAINS(String::contains),
        EQUALS(String::equals),
        STARTS_WITH(String::startsWith),
        ENDS_WITH(String::endsWith),
        MATCHES(String::matches);
        
        private final Compare<String> method;
        
        FilterMethod(Compare<String> method) {
            this.method = method;
        }

        public Compare<String> getMethod() {
            return method;
        }
    }

    @FunctionalInterface
    interface Compare<T> {
        boolean compare(T obj1, T obj2);
    }
    
}
