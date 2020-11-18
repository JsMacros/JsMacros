package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.api.events.EventKey;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.macros.EventMacro;
import xyz.wagyourtail.jsmacros.macros.KeyMacro;

import java.util.*;

public class EventRegistry implements IEventRegistry {
    public final Map<String, Set<IEventListener>> macros = new LinkedHashMap<>();
    public final Map<String, String> oldEvents = new LinkedHashMap<>();
    public final Set<String> events = new LinkedHashSet<>();
    
    public EventRegistry() {
        addEvent("ANYTHING");
    }
    
    public synchronized void clearMacros() {
        macros.clear();
    }
    
    public synchronized void addRawMacro(ScriptTrigger rawmacro) {
        switch (rawmacro.triggerType) {
            case KEY_RISING:
            case KEY_FALLING:
            case KEY_BOTH:
                addListener(EventKey.class.getSimpleName(), new KeyMacro(rawmacro));
                return;
            case EVENT:
                if (oldEvents.containsKey(rawmacro.event)) {
                    rawmacro.event = oldEvents.get(rawmacro.event);
                }
                addListener(rawmacro.event, new EventMacro(rawmacro));
                return;
            default:
                System.out.println("Failed To Add: Unknown macro type for file " + rawmacro.scriptFile.toString());
                return;
        }
    }
    
    public synchronized void addListener(String event, IEventListener listener) {
        macros.putIfAbsent(event, new LinkedHashSet<>());
        macros.get(event).add(listener);
    }
    
    public synchronized boolean removeListener(String event, IEventListener listener) {
        macros.putIfAbsent(event, new LinkedHashSet<>());
        return macros.get(event).remove(listener);
    }
    
    public synchronized boolean removeListener(IEventListener listener) {
        for (Set<IEventListener> listeners : macros.values()) {
            if (listeners.contains(listener)) {
                return listeners.remove(listener);
            }
        }
        return false;
    }
    
    public synchronized boolean removeRawMacro(ScriptTrigger rawmacro) {
        final String event = rawmacro.triggerType == IEventTrigger.TriggerType.EVENT ? rawmacro.event : EventKey.class.getSimpleName();
        for (IEventListener macro : macros.get(event)) {
            if (macro instanceof BaseMacro && ((BaseMacro) macro).getRawMacro() == rawmacro) {
                removeListener(event, macro);
                return true;
            }
        }
        return false;
    }
    
    public synchronized Map<String, Set<IEventListener>> getListeners() {
        return macros;
    }
    
    public synchronized Set<IEventListener> getListeners(String key) {
        return macros.get(key);
    }
    
    public synchronized List<ScriptTrigger> getRawMacros() {
        final List<ScriptTrigger> rawProf = new ArrayList<>();
        for (Set<IEventListener> eventMacros : macros.values()) {
            for (IEventListener macro : eventMacros) {
                if (macro instanceof BaseMacro) rawProf.add(((BaseMacro) macro).getRawMacro());
            }
        }
        return rawProf;
    }
    
    public synchronized void addEvent(String eventName) {
        events.add(eventName);
    }
    
    public synchronized void addEvent(Class<? extends BaseEvent> clazz) {
        if (clazz.isAnnotationPresent(Event.class)) {
            Event e = clazz.getAnnotation(Event.class);
            if (!e.oldName().equals("")) oldEvents.put(e.oldName(), "Event"+e.value());
            events.add("Event"+e.value());
        } else {
            throw new RuntimeException("Tried to add event that doesn't have proper event annotation, " + clazz.getSimpleName());
        }
    }
    
    @Deprecated
    public synchronized void addEvent(String oldName, Class<? extends BaseEvent> clazz) {
        oldEvents.put(oldName, clazz.getSimpleName());
        events.add(clazz.getSimpleName());
    }
    
    public synchronized BaseMacro getMacro(ScriptTrigger scriptTrigger) {
        for (Set<IEventListener> eventMacros : macros.values()) {
            for (IEventListener macro : eventMacros) {
                if (macro instanceof BaseMacro && scriptTrigger == ((BaseMacro) macro).getRawMacro()) return (BaseMacro) macro;
            }
        }
        return null;
    }
}
