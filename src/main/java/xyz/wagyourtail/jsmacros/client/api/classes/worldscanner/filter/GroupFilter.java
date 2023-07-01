package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.api.IFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.compare.NumberCompareFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public abstract class GroupFilter<T> implements IFilter<T> {

    protected List<IFilter<T>> filters;

    protected GroupFilter() {
        this.filters = new ArrayList<>();
    }

    public GroupFilter<T> add(IFilter<T> filter) {
        this.filters.add(filter);
        return this;
    }

    public GroupFilter<T> add(List<IFilter<T>> filters) {
        this.filters.addAll(filters);
        return this;
    }

    public GroupFilter<T> remove(IFilter<T> filter) {
        this.filters.remove(filter);
        return this;
    }

    public GroupFilter<T> remove(List<IFilter<T>> filters) {
        this.filters.removeAll(filters);
        return this;
    }

    public List<IFilter<T>> getFilters() {
        return ImmutableList.copyOf(filters);
    }

    public static class AllMatchFilter<T> extends GroupFilter<T> {

        public AllMatchFilter() {
            super();
        }

        @Override
        public Boolean apply(T t) {
            return filters.stream().allMatch(filter -> filter.apply(t));
        }

    }

    public static class AnyMatchFilter<T> extends GroupFilter<T> {

        public AnyMatchFilter() {
            super();
        }

        @Override
        public Boolean apply(T t) {
            return filters.stream().anyMatch(filter -> filter.apply(t));
        }

    }

    public static class NoneMatchFilter<T> extends GroupFilter<T> {

        public NoneMatchFilter() {
            super();
        }

        @Override
        public Boolean apply(T t) {
            return filters.stream().noneMatch(filter -> filter.apply(t));
        }

    }

    public static class CountMatchFilter<T> extends GroupFilter<T> {

        private final IFilter<Number> filter;

        public CountMatchFilter(String operation, long compareTo) {
            super();
            filter = new NumberCompareFilter(operation, compareTo);
        }

        @Override
        public Boolean apply(T t) {
            return filter.apply(filters.stream().filter(filter -> filter.apply(t)).count());
        }

    }

}
