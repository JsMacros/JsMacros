package xyz.wagyourtail.jsmacros.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.macros.EventMacro;
import xyz.wagyourtail.jsmacros.macros.KeyBothMacro;
import xyz.wagyourtail.jsmacros.macros.KeyDownMacro;
import xyz.wagyourtail.jsmacros.macros.KeyUpMacro;

public class MacroRegistry {
    public Map<String, Map<RawMacro, BaseMacro>> macros;
    public List<String> events = new ArrayList<>();
    
    public void clearMacros() {
        macros = new HashMap<>();
    }
    
    public void addMacro(RawMacro rawmacro) {
        switch (rawmacro.type) {
            case KEY_RISING:
                macros.putIfAbsent("KEY", new HashMap<>());
                macros.get("KEY").put(rawmacro, new KeyDownMacro(rawmacro));
                break;
            case KEY_FALLING:
                macros.putIfAbsent("KEY", new HashMap<>());
                macros.get("KEY").put(rawmacro, new KeyUpMacro(rawmacro));
                break;
            case KEY_BOTH:
                macros.putIfAbsent("KEY", new HashMap<>());
                macros.get("KEY").put(rawmacro, new KeyBothMacro(rawmacro));
                break;
            case EVENT:
                macros.putIfAbsent(rawmacro.eventkey, new HashMap<>());
                macros.get(rawmacro.eventkey).put(rawmacro, new EventMacro(rawmacro));
                break;
            default:
                System.out.println("Failed To Add: Unknown macro type for file " + rawmacro.scriptFile.toString());
                break;
        }
    }
    
    public void removeMacro(RawMacro rawmacro) {
        switch (rawmacro.type) {
            case KEY_RISING:
            case KEY_FALLING:
            case KEY_BOTH:
                macros.putIfAbsent("KEY", new HashMap<>());
                macros.get("KEY").remove(rawmacro);
                break;
            case EVENT:
                macros.putIfAbsent(rawmacro.eventkey, new HashMap<>());
                macros.get(rawmacro.eventkey).remove(rawmacro);
                break;
            default:
                System.out.println("Failed To Remove: Unknown macro type for file " + rawmacro.scriptFile.toString());
                break;
        }
    }
    
    public Map<String, Map<RawMacro, BaseMacro>> getMacros() {
        return macros;
    }
    
    public void addEvent(String eventName) {
        if (!events.contains(eventName)) events.add(eventName);
    }
    
    public BaseMacro getMacro(RawMacro rawMacro) {
        for (Map<RawMacro, BaseMacro> eventMacros : macros.values()) {
            for (RawMacro macro : eventMacros.keySet()) {
                if (rawMacro == macro) return eventMacros.get(macro);
            }
        }
        return null;
    }
}
