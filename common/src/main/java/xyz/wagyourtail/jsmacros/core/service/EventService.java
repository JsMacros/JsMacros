package xyz.wagyourtail.jsmacros.core.service;

import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("Service")
public class EventService implements BaseEvent {
    public final String serviceName;

    /**
     * when this service is stopped, this is run...
     */
    public MethodWrapper<Object, Object, Object, ?> stopListener;

    public EventService(String name) {
        this.serviceName = name;
    }
}
