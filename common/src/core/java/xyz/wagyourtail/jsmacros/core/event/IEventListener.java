package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

@FunctionalInterface
public interface IEventListener {
    
    EventContainer<?> trigger(BaseEvent event);
    
    default void off() {
        Core.getInstance().eventRegistry.removeListener(this);
    }
    
}
