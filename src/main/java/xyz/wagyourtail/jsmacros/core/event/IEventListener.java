package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

public interface IEventListener {
    
    public Thread trigger(BaseEvent event);
}
