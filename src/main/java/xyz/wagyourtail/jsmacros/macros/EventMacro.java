package xyz.wagyourtail.jsmacros.macros;

import java.util.HashMap;

import xyz.wagyourtail.jsmacros.config.RawMacro;

public class EventMacro extends BaseMacro {
    
    public EventMacro(RawMacro macro) {
        super(macro);
    }
    
    public Thread trigger(String type, HashMap<String, Object> args) {
        return runMacro(type, args);
    }
}