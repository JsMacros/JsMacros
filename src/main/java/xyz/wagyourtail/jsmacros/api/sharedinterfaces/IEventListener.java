package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

import java.util.Map;

public interface IEventListener {
    
    public Thread trigger(String type, Map<String, Object> args);
}
