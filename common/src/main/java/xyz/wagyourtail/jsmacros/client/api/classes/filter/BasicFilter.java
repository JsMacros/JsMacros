package xyz.wagyourtail.jsmacros.client.api.classes.filter;

import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.logical.AndFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.logical.NotFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.logical.OrFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IAdvancedFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.logical.XorFilter;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public abstract class BasicFilter<T> implements IFilter<T>, IAdvancedFilter<T> {

    @Override
    public IFilter<T> and(IFilter<T> filter) {
        return new AndFilter<>(this, filter);
    }

    @Override
    public IFilter<T> or(IFilter<T> filter) {
        return new OrFilter<>(this, filter);
    }

    @Override
    public IFilter<T> xor(IFilter<T> filter) {
        return new XorFilter<>(this, filter);
    }

    @Override
    public IFilter<T> not() {
        return new NotFilter<>(this);
    }

}
