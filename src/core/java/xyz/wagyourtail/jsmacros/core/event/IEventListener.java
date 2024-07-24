package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

public interface IEventListener {
    boolean joined();

    EventContainer<?> trigger(BaseEvent event);

    /**
     * Used for self unregistering events.
     *
     * @since 1.8.4
     */
    default void off() {}

}
