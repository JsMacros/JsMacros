package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilterer;

import java.util.LinkedList;
import java.util.List;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
public class FiltererComposed implements EventFilterer.Compound {
    private final LinkedList<List<EventFilterer>> components = new LinkedList<>();

    public FiltererComposed(EventFilterer initial) {
        or(initial);
    }

    @Override
    public boolean canFilter(String event) {
        for (List<EventFilterer> c : components) {
            for (EventFilterer f : c) {
                if (!f.canFilter(event)) return false;
            }
        }
        return true;
    }

    @Override
    public boolean test(BaseEvent event) {
        outer:
        for (List<EventFilterer> c : components) {
            for (EventFilterer f : c) {
                if (!f.test(event)) continue outer;
            }
            return true;
        }
        return false;
    }

    /**
     * @param filterer the filterer to compose
     * @return self for chaining
     */
    public FiltererComposed and(EventFilterer filterer) {
        if (filterer == null) throw new IllegalArgumentException("filterer cannot be null!");
        if (filterer instanceof FiltererComposed fc) fc.checkCyclicRef(this);

        components.getLast().add(filterer);
        return this;
    }

    /**
     * @param filterer the filterer to compose
     * @return self for chaining
     */
    public FiltererComposed or(EventFilterer filterer) {
        if (filterer == null) throw new IllegalArgumentException("filterer cannot be null!");
        if (filterer instanceof FiltererComposed fc) fc.checkCyclicRef(this);

        List<EventFilterer> list = new LinkedList<>();
        list.add(filterer);
        components.add(list);
        return this;
    }

    public void checkCyclicRef(Compound base) {
        Compound.super.checkCyclicRef(base);
        for (List<EventFilterer> c : components) {
            for (EventFilterer f : c) {
                if (f instanceof Compound fc) fc.checkCyclicRef(base);
            }
        }
    }

}
