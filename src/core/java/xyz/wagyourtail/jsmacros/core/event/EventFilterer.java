package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.doclet.DocletReplaceParams;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
public interface EventFilterer {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @DocletReplaceParams("event: keyof Events")
    boolean canFilter(String event);

    boolean test(BaseEvent event);

    interface Compound extends EventFilterer {

        default void checkCyclicRef(Compound base) {
            if (this == base) throw new IllegalArgumentException("Cyclic reference detected.");
        }

    }

}
