package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilterer;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
public class FiltererInverted implements EventFilterer.Compound {
    public final EventFilterer base;

    public static EventFilterer invert(EventFilterer base) {
        if (base == null) throw new IllegalArgumentException("base cannot be null!");
        if (base.getClass() == FiltererInverted.class) return ((FiltererInverted) base).base;
        return new FiltererInverted(base);
    }

    private FiltererInverted(EventFilterer base) {
        this.base = base;
    }

    @Override
    public boolean canFilter(String event) {
        return base.canFilter(event);
    }

    @Override
    public boolean test(BaseEvent event) {
        return !base.test(event);
    }

    @Override
    public void checkCyclicRef(Compound base) {
        Compound.super.checkCyclicRef(base);
        if (this.base instanceof Compound c) c.checkCyclicRef(base);
    }

}
