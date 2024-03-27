package xyz.wagyourtail.jsmacros.core.event;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.ApiStatus;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros;

import java.util.*;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public abstract class BaseEventRegistry {
    protected final Core runner;
    protected final Map<String, Set<IEventListener>> listeners = new LinkedHashMap<>();
    public final Map<String, String> oldEvents = new LinkedHashMap<>();
    public final Set<String> events = new LinkedHashSet<>();
    public final Set<String> cancellableEvents = new HashSet<>();
    public final Set<String> joinableEvents = new HashSet<>();
    public final Map<String, Class<? extends EventFilterer>> filterableEvents = new HashMap<>();

    public BaseEventRegistry(Core runner) {
        this.runner = runner;
    }

    public synchronized void clearMacros() {
        for (Set<IEventListener> value : listeners.values()) {
            value.removeIf(listener -> !(listener instanceof FJsMacros.ScriptEventListener));
        }
    }

    /**
     * @param rawmacro
     * @since 1.1.2 [citation needed]
     */
    public abstract void addScriptTrigger(ScriptTrigger rawmacro);

    /**
     * @param event
     * @param listener
     * @since 1.2.3
     */
    public synchronized void addListener(String event, IEventListener listener) {
        listeners.putIfAbsent(event, new LinkedHashSet<>());
        listeners.get(event).add(listener);
    }

    /**
     * @param event
     * @param listener
     * @return
     * @since 1.2.3
     */
    public synchronized boolean removeListener(String event, IEventListener listener) {
        listeners.putIfAbsent(event, new LinkedHashSet<>());
        return listeners.get(event).remove(listener);
    }

    /**
     * @param listener
     * @return
     * @since 1.2.3
     */
    @Deprecated
    public synchronized boolean removeListener(IEventListener listener) {
        for (Set<IEventListener> listeners : listeners.values()) {
            if (listeners.contains(listener)) {
                return listeners.remove(listener);
            }
        }
        return false;
    }

    /**
     * @param rawmacro
     * @return
     * @since 1.1.2 [citation needed]
     */
    public abstract boolean removeScriptTrigger(ScriptTrigger rawmacro);

    /**
     * @return
     * @since 1.2.3
     */
    public synchronized Map<String, Set<IEventListener>> getListeners() {
        return listeners;
    }

    /**
     * @param key
     * @return
     * @since 1.2.3
     */
    public synchronized Set<IEventListener> getListeners(String key) {
        return ImmutableSet.copyOf(listeners.computeIfAbsent(key, (k) -> new LinkedHashSet<>()));
    }

    /**
     * @return
     * @see ScriptTrigger
     * @since 1.1.2 [citation needed]
     */
    public abstract List<ScriptTrigger> getScriptTriggers();

    /**
     * @param eventName
     * @since 1.1.2 [citation needed]
     */
    @ApiStatus.Internal
    public synchronized void addEvent(String eventName) {
        events.add(eventName);
    }

    @ApiStatus.Internal
    public synchronized void addEvent(String eventName, boolean joinable) {
        events.add(eventName);
        if (joinable) {
            joinableEvents.add(eventName);
        }
    }

    @ApiStatus.Internal
    public synchronized void addEvent(String eventName, boolean joinable, boolean cancellable) {
        events.add(eventName);
        if (joinable || cancellable) {
            joinableEvents.add(eventName);
        }
        if (cancellable) {
            cancellableEvents.add(eventName);
        }
    }


    public synchronized void addEvent(Class<? extends BaseEvent> clazz) {
        if (clazz.isAnnotationPresent(Event.class)) {
            Event e = clazz.getAnnotation(Event.class);
            if (!e.oldName().isEmpty()) {
                oldEvents.put(e.oldName(), e.value());
            }
            oldEvents.put(clazz.getSimpleName(), e.value());
            events.add(e.value());
            if (e.cancellable()) {
                cancellableEvents.add(e.value());
                joinableEvents.add(e.value());
            }
            if (e.joinable()) {
                joinableEvents.add(e.value());
            }
            if (e.filterer() != EventFilterer.class) {
                filterableEvents.put(e.value(), e.filterer());
            }
        } else {
            throw new RuntimeException("Tried to add event that doesn't have proper event annotation, " + clazz.getSimpleName());
        }
    }

}
