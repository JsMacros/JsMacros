package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilterer;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class FiltererModulus implements EventFilterer {
    public int quotient;
    public int count = 0;

    public FiltererModulus(int quotient) {
        this.quotient = Math.abs(quotient);
    }

    @Override
    public boolean canFilter(String event) {
        return true;
    }

    @Override
    public boolean test(BaseEvent event) {
        if (++count >= quotient) {
            count = 0;
            return true;
        }
        return false;
    }

    public FiltererModulus setQuotient(int quotient) {
        this.quotient = Math.abs(quotient);
        return this;
    }

}
