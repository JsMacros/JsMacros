package xyz.wagyourtail.jsmacros.macros;

import java.util.Map;

import xyz.wagyourtail.jsmacros.config.RawMacro;

public class EventMacro extends BaseMacro {
    
    public EventMacro(RawMacro macro) {
        super(macro);
    }
    
    @Override
    public Thread trigger(String type, Map<String, Object> args) {
        return runMacro(type, args);
    }
}