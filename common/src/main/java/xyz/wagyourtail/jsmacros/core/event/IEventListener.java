package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.language.EventContainer;

public interface IEventListener {
    
    EventContainer<?> trigger(BaseEvent event);
}
