package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.compare;

import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.api.IFilter;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class BooleanCompareFilter implements IFilter<Boolean> {

    private final boolean compareTo;

    public BooleanCompareFilter(boolean compareTo) {
        this.compareTo = compareTo;
    }

    @Override
    public Boolean apply(Boolean bool) {
        return bool.equals(compareTo);
    }

}
