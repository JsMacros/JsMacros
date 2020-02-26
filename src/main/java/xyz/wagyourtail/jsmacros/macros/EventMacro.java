package xyz.wagyourtail.jsmacros.macros;

import java.util.HashMap;

import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.events.EventTypesEnum;

public class EventMacro extends BaseMacro {
    
    public EventMacro(RawMacro macro) {
        super(macro);
    }
    
    public Thread trigger(EventTypesEnum type, HashMap<String, Object> args) {
        return runMacro(type, args);
    }
}