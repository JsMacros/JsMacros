package xyz.wagyourtail.jsmacros.client.api.classes.filter;

import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IAdvancedFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.logical.AndFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.logical.NotFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.logical.OrFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.logical.XorFilter;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public abstract class BasicFilter<T> implements IAdvancedFilter<T> {

    @Override
    public IAdvancedFilter<T> and(IFilter<T> filter) {
        return new AndFilter<>(this, filter);
    }

    @Override
    public IAdvancedFilter<T> or(IFilter<T> filter) {
        return new OrFilter<>(this, filter);
    }

    @Override
    public IAdvancedFilter<T> xor(IFilter<T> filter) {
        return new XorFilter<>(this, filter);
    }

    @Override
    public IAdvancedFilter<T> not() {
        return new NotFilter<>(this);
    }

}
