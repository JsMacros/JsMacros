package xyz.wagyourtail.jsmacros.core.event;

public interface IEventListener {
    
    Thread trigger(BaseEvent event);
}
