package xyz.wagyourtail.jsmacros.client;

import xyz.wagyourtail.jsmacros.client.api.events.EventKey;
import xyz.wagyourtail.jsmacros.client.listeners.KeyListener;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventRegistry extends BaseEventRegistry {
    
    public EventRegistry(Core<?> runner) {
        super(runner);
    }
    
    public synchronized void addScriptTrigger(ScriptTrigger rawmacro) {
        switch (rawmacro.triggerType) {
            case KEY_RISING:
            case KEY_FALLING:
            case KEY_BOTH:
                addListener(EventKey.class.getAnnotation(Event.class).value(), new KeyListener(rawmacro, runner));
                return;
            case EVENT:
                if (oldEvents.containsKey(rawmacro.event)) {
                    rawmacro.event = oldEvents.get(rawmacro.event);
                }
                addListener(rawmacro.event, new EventListener(rawmacro, runner));
                return;
            default:
                System.out.println("Failed To Add: Unknown macro type for file " + rawmacro.scriptFile);
        }
    }
    
    
    public synchronized boolean removeScriptTrigger(ScriptTrigger rawmacro) {
        final String event = rawmacro.triggerType == ScriptTrigger.TriggerType.EVENT ? rawmacro.event : EventKey.class.getAnnotation(Event.class).value();
        for (IEventListener macro : listeners.get(event)) {
            if (macro instanceof BaseListener && ((BaseListener) macro).getRawTrigger() == rawmacro) {
                removeListener(event, macro);
                return true;
            }
        }
        return false;
    }
    
    public synchronized List<ScriptTrigger> getScriptTriggers() {
        final List<ScriptTrigger> rawProf = new ArrayList<>();
        for (Set<IEventListener> eventMacros : listeners.values()) {
            for (IEventListener macro : eventMacros) {
                if (macro instanceof BaseListener) rawProf.add(((BaseListener) macro).getRawTrigger());
            }
        }
        return rawProf;
    }
}
