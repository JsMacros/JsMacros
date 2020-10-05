package xyz.wagyourtail.jsmacros.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xyz.wagyourtail.jsmacros.api.events.EventKey;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEventListener;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEventRegistry;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.macros.EventMacro;
import xyz.wagyourtail.jsmacros.macros.KeyMacro;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;

public class EventRegistry implements IEventRegistry {
    public Map<String, Set<IEventListener>> macros;
    public Map<String, String> oldEvents = new LinkedHashMap<>();
    public Set<String> events = new LinkedHashSet<>();
    
    public void clearMacros() {
        macros = new HashMap<>();
    }
    
    public void addRawMacro(RawMacro rawmacro) {
        switch (rawmacro.type) {
            case KEY_RISING:
            case KEY_FALLING:
            case KEY_BOTH:
                addListener(EventKey.class.getSimpleName(), new KeyMacro(rawmacro));
                return;
            case EVENT:
                if (oldEvents.containsKey(rawmacro.eventkey)) {
                    rawmacro.eventkey = oldEvents.get(rawmacro.eventkey);
                }
                addListener(rawmacro.eventkey, new EventMacro(rawmacro));
                return;
            default:
                System.out.println("Failed To Add: Unknown macro type for file " + rawmacro.scriptFile.toString());
                return;
        }
    }
    
    public void addListener(String event, IEventListener listener) {
        macros.putIfAbsent(event, new LinkedHashSet<>());
        macros.get(event).add(listener);
    }
    
    public boolean removeListener(String event, IEventListener listener) {
        macros.putIfAbsent(event, new LinkedHashSet<>());
        return macros.get(event).remove(listener);
    }
    
    public boolean removeListener(IEventListener listener) {
        for (Set<IEventListener> listeners : macros.values()) {
            if (listeners.contains(listener)) {
                return listeners.remove(listener);
            }
        }
        return false;
    }
    
    public boolean removeRawMacro(RawMacro rawmacro) {
        String event = rawmacro.type == MacroEnum.EVENT ? rawmacro.eventkey : EventKey.class.getSimpleName();
        for (IEventListener macro : macros.get(event)) {
            if (macro instanceof BaseMacro && ((BaseMacro) macro).getRawMacro() == rawmacro) {
                removeListener(event, macro);
                return true;
            }
        }
        return false;
    }
    
    public Map<String, Set<IEventListener>> getListeners() {
        return macros;
    }
    
    public Set<IEventListener> getListeners(String key) {
        return macros.get(key);
    }
    
    public List<RawMacro> getRawMacros() {
        List<RawMacro> rawProf = new ArrayList<>();
        for (Set<IEventListener> eventMacros : macros.values()) {
            for (IEventListener macro : eventMacros) {
                if (macro instanceof BaseMacro) rawProf.add(((BaseMacro) macro).getRawMacro());
            }
        }
        return rawProf;
    }
    
    public void addEvent(String eventName) {
        events.add(eventName);
    }
    
    public void addEvent(Class<? extends IEvent> clazz) {
        events.add(clazz.getSimpleName());
    }
    
    public void addEvent(String oldName, Class<? extends IEvent> clazz) {
        oldEvents.put(oldName, clazz.getSimpleName());
        events.add(clazz.getSimpleName());
    }
    
    public BaseMacro getMacro(RawMacro rawMacro) {
        for (Set<IEventListener> eventMacros : macros.values()) {
            for (IEventListener macro : eventMacros) {
                if (macro instanceof BaseMacro && rawMacro == ((BaseMacro) macro).getRawMacro()) return (BaseMacro) macro;
            }
        }
        return null;
    }
}
