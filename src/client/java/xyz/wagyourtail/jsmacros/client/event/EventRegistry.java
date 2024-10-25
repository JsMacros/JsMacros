package xyz.wagyourtail.jsmacros.client.event;

import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;
import xyz.wagyourtail.jsmacros.client.listeners.KeyListener;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EventRegistry extends BaseEventRegistry {

    public EventRegistry(Core<?, ?> runner) {
        super(runner);
    }

    @Override
    public synchronized void addScriptTrigger(ScriptTrigger rawmacro) {
        if (oldEvents.containsKey(rawmacro.event)) {
            rawmacro.event = oldEvents.get(rawmacro.event);
        }
        if (rawmacro.triggerType == ScriptTrigger.TriggerType.EVENT) {
            if (rawmacro.event.startsWith("Joined")) {
                rawmacro.event = rawmacro.event.substring(6);
                rawmacro.joined = true;
            }
            addListener(rawmacro.event, new EventListener(rawmacro, runner));
        } else {
            addListener(EventKey.class.getAnnotation(Event.class).value(), new KeyListener(rawmacro, runner));
        }
    }

    @Override
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

    @Override
    public synchronized List<ScriptTrigger> getScriptTriggers() {
        final List<ScriptTrigger> rawProf = new ArrayList<>();
        for (Set<IEventListener> eventMacros : listeners.values()) {
            for (IEventListener macro : eventMacros) {
                if (macro instanceof BaseListener) {
                    rawProf.add(((BaseListener) macro).getRawTrigger());
                }
            }
        }
        return rawProf;
    }

}
