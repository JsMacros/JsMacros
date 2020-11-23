package xyz.wagyourtail.jsmacros.core.event;

public interface IEventListener {
    
    public Thread trigger(BaseEvent event);
}
