package xyz.wagyourtail.jsmacros.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.macros.EventMacro;
import xyz.wagyourtail.jsmacros.macros.IEventListener;
import xyz.wagyourtail.jsmacros.macros.KeyBothMacro;
import xyz.wagyourtail.jsmacros.macros.KeyDownMacro;
import xyz.wagyourtail.jsmacros.macros.KeyUpMacro;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;

public class EventRegistry {
    public Map<String, List<IEventListener>> macros;
    public List<String> events = new ArrayList<>();
    
    public void clearMacros() {
        macros = new HashMap<>();
    }
    
    public void addRawMacro(RawMacro rawmacro) {
        switch (rawmacro.type) {
            case KEY_RISING:
                addListener("KEY", new KeyDownMacro(rawmacro));
                return;
            case KEY_FALLING:
                addListener("KEY", new KeyUpMacro(rawmacro));
                return;
            case KEY_BOTH:
                addListener("KEY", new KeyBothMacro(rawmacro));
                return;
            case EVENT:
                addListener(rawmacro.eventkey, new EventMacro(rawmacro));
                return;
            default:
                System.out.println("Failed To Add: Unknown macro type for file " + rawmacro.scriptFile.toString());
                return;
        }
    }
    
    public void addListener(String event, IEventListener listener) {
        macros.putIfAbsent(event, new ArrayList<>());
        macros.get(event).add(listener);
    }
    
    public void removeListener(String event, IEventListener listener) {
        macros.putIfAbsent(event, new ArrayList<>());
        macros.get(event).remove(listener);
    }
    
    public void removeListener(IEventListener listener) {
        for (List<IEventListener> listeners : macros.values()) {
            if (listeners.contains(listener)) {
                listeners.remove(listener);
                return;
            }
        }
    }
    
    public boolean removeRawMacro(RawMacro rawmacro) {
        String event = rawmacro.type == MacroEnum.EVENT ? rawmacro.eventkey : "KEY";
        for (IEventListener macro : macros.get(event)) {
            if (macro instanceof BaseMacro && ((BaseMacro) macro).getRawMacro() == rawmacro) {
                removeListener(event, macro);
                return true;
            }
        }
        return false;
    }
    
    public Map<String, List<IEventListener>> getListeners() {
        return macros;
    }
    
    public List<IEventListener> getListeners(String key) {
        return macros.get(key);
    }
    
    public List<RawMacro> getRawMacros() {
        List<RawMacro> rawProf = new ArrayList<>();
        for (List<IEventListener> eventMacros : macros.values()) {
            for (IEventListener macro : eventMacros) {
                if (macro instanceof BaseMacro) rawProf.add(((BaseMacro) macro).getRawMacro());
            }
        }
        return rawProf;
    }
    
    public void addEvent(String eventName) {
        if (!events.contains(eventName)) events.add(eventName);
    }
    
    public BaseMacro getMacro(RawMacro rawMacro) {
        for (List<IEventListener> eventMacros : macros.values()) {
            for (IEventListener macro : eventMacros) {
                if (macro instanceof BaseMacro && rawMacro == ((BaseMacro) macro).getRawMacro()) return (BaseMacro) macro;
            }
        }
        return null;
    }
}
