package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.logical;

import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.BasicFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.api.IFilter;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class NotFilter<T> extends BasicFilter<T> {

    private final IFilter<T> filter;

    public NotFilter(IFilter<T> filter) {
        this.filter = filter;
    }

    @Override
    public Boolean apply(T obj) {
        return !filter.apply(obj);
    }

    public IFilter<T> getFilter() {
        return filter;
    }

}
