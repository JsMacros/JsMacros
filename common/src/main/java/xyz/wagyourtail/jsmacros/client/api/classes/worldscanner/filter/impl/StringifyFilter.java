package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.impl;

import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.BasicFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.api.ICompare;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.compare.StringCompareFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class StringifyFilter<T> extends BasicFilter<T> {

    private final Set<String> filterObjects;
    private final ICompare<String> filter;

    public StringifyFilter(String operation) {
        this.filterObjects = new HashSet<>();
        filter = StringCompareFilter.FilterMethod.valueOf(operation).getMethod();
    }

    public StringifyFilter addOption(String toAdd) {
        filterObjects.add(toAdd);
        return this;
    }

    public StringifyFilter addOption(String... toAdd) {
        filterObjects.addAll(List.of(toAdd));
        return this;
    }

    public StringifyFilter removeOption(String toRemove) {
        filterObjects.remove(toRemove);
        return this;
    }

    public StringifyFilter removeOption(String... toRemove) {
        List.of(toRemove).forEach(filterObjects::remove);
        return this;
    }

    @Override
    public Boolean apply(Object obj) {
        String toCompare = obj.toString();
        return filterObjects.parallelStream().anyMatch(filterElement -> filter.compare(toCompare, filterElement));
    }

}
