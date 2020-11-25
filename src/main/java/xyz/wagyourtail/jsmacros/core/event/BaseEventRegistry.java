package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.util.*;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public abstract class BaseEventRegistry {
    protected final Core runner;
    public final Map<String, Set<IEventListener>> listeners = new LinkedHashMap<>();
    public final Map<String, String> oldEvents = new LinkedHashMap<>();
    
    public final Set<String> events = new LinkedHashSet<>();
    
    public BaseEventRegistry(Core runner) {
        this.runner = runner;
    }
    
    public synchronized void clearMacros() {
        listeners.clear();
    }
    
    /**
     * @since 1.1.2 [citation needed]
     * @param rawmacro
     */
    public abstract void addScriptTrigger(ScriptTrigger rawmacro);
    
    /**
     * @since 1.2.3
     * @param event
     * @param listener
     */
    public synchronized void addListener(String event, IEventListener listener) {
        listeners.putIfAbsent(event, new LinkedHashSet<>());
        listeners.get(event).add(listener);
    }
    
    /**
     * @since 1.2.3
     * @param event
     * @param listener
     * @return
     */
    public synchronized boolean removeListener(String event, IEventListener listener) {
        listeners.putIfAbsent(event, new LinkedHashSet<>());
        return listeners.get(event).remove(listener);
    }
    
    /**
     * @since 1.2.3
     * @param listener
     * @return
     */
    public synchronized boolean removeListener(IEventListener listener) {
        for (Set<IEventListener> listeners : listeners.values()) {
            if (listeners.contains(listener)) {
                return listeners.remove(listener);
            }
        }
        return false;
    }
    
    /**
     * @since 1.1.2 [citation needed]
     * @param rawmacro
     * @return
     */
    public abstract boolean removeScriptTrigger(ScriptTrigger rawmacro);
    
    /**
     * @since 1.2.3
     * @return
     */
    public synchronized Map<String, Set<IEventListener>> getListeners() {
        return listeners;
    }
    
    /**
     * @since 1.2.3
     * @param key
     * @return
     */
    public synchronized Set<IEventListener> getListeners(String key) {
        return listeners.get(key);
    }
    
    /**
     * @see ScriptTrigger
     * @since 1.1.2 [citation needed]
     * @return
     */
    public abstract List<ScriptTrigger> getScriptTriggers();
    
    /**
     * @since 1.1.2 [citation needed]
     * @param eventName
     */
    public synchronized void addEvent(String eventName) {
        events.add(eventName);
    }
    
    public synchronized void addEvent(Class<? extends BaseEvent> clazz) {
        if (clazz.isAnnotationPresent(Event.class)) {
            Event e = clazz.getAnnotation(Event.class);
            if (!e.oldName().equals("")) oldEvents.put(e.oldName(), e.value());
            oldEvents.put(clazz.getSimpleName(), e.value());
            events.add(e.value());
        } else {
            throw new RuntimeException("Tried to add event that doesn't have proper event annotation, " + clazz.getSimpleName());
        }
    }

}
