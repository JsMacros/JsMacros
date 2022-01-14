package xyz.wagyourtail.jsmacros.client.api.sharedinterfaces;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public interface IAdvancedFilter<T> {

    IFilter<T> and(IFilter<T> filter);
    IFilter<T> or(IFilter<T> filter);
    IFilter<T> not();
    
}
