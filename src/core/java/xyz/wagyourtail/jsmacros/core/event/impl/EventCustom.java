package xyz.wagyourtail.jsmacros.core.event.impl;

import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Events
 *
 * @author Wagyourtail
 * @since 1.2.8
 */
@Event("Custom")
@SuppressWarnings("unused")
public class EventCustom extends BaseEvent {
    protected Map<String, Object> args = new ConcurrentHashMap<>();
    public String eventName;
    public boolean joinable;
    public boolean cancelable;

    @Override
    public boolean joinable() {
        return joinable || cancelable;
    }

    @Override
    public boolean cancellable() {
        return cancelable;
    }

    /**
     * @param eventName name of the event. please don't use an existing one... your scripts might not like that.
     */
    public EventCustom(Core<?, ?> runner, String eventName) {
        super(runner);
        this.eventName = eventName;
    }

    /**
     * Triggers the event.
     * Try not to cause infinite looping by triggering the same {@link EventCustom} from its own listeners.
     *
     * @since 1.2.8
     */
    public void trigger() {
        runner.profile.triggerEvent(this);
    }

    /**
     * trigger the event listeners, then run {@code callback} when they finish.
     *
     * @param callback used as a {@link Runnable}, so no args, no return value.
     * @since 1.9.0
     */
    public void triggerAsync(MethodWrapper<Object, Object, Object, ?> callback) {
        runner.threadPool.runTask(() -> {
            runner.profile.triggerEvent(this);
            try {
                callback.run();
            } catch (Throwable e) {
                runner.profile.logError(e);
            }
        });
    }

    /**
     * Put an Integer into the event.
     *
     * @param name
     * @param i
     * @return
     * @since 1.2.8
     */
    public int putInt(String name, int i) {
        args.put(name, i);
        return i;
    }

    /**
     * put a String into the event.
     *
     * @param name
     * @param str
     * @return
     * @since 1.2.8
     */
    public String putString(String name, String str) {
        args.put(name, str);
        return str;
    }

    /**
     * put a Double into the event.
     *
     * @param name
     * @param d
     * @return
     * @since 1.2.8
     */
    public double putDouble(String name, double d) {
        args.put(name, d);
        return d;
    }

    /**
     * put a Boolean into the event.
     *
     * @param name
     * @param b
     * @return
     * @since 1.2.8
     */
    public boolean putBoolean(String name, boolean b) {
        args.put(name, b);
        return b;
    }

    /**
     * put anything else into the event.
     *
     * @param name
     * @param o
     * @return
     * @since 1.2.8
     */
    public Object putObject(String name, Object o) {
        if (runner.extensions.isGuestObject(o)) {
            throw new AssertionError("Cannot put a guest object into an event");
        }
        args.put(name, o);
        return o;
    }

    /**
     * Returns the type of the defined item in the event as a string.
     *
     * @param name
     * @return
     * @since 1.2.8
     */
    @DocletReplaceReturn("'Int' | 'String' | 'Double' | 'Boolean' | 'Object' | null")
    @Nullable
    public String getType(String name) {
        Object i = args.get(name);
        if (i == null) {
            return null;
        } else if (i instanceof Integer) {
            return "Int";
        } else if (i instanceof String) {
            return "String";
        } else if (i instanceof Double) {
            return "Double";
        } else if (i instanceof Boolean) {
            return "Boolean";
        } else {
            return "Object";
        }
    }

    /**
     * Gets an Integer from the event.
     *
     * @param name
     * @return
     * @since 1.2.8
     */
    @Nullable
    public Integer getInt(String name) {
        Object i = args.get(name);
        if (i instanceof Integer) {
            return (Integer) i;
        } else {
            return null;
        }
    }

    /**
     * Gets a String from the event
     *
     * @param name
     * @return
     * @since 1.2.8
     */
    @Nullable
    public String getString(String name) {
        Object i = args.get(name);
        if (i instanceof String) {
            return (String) i;
        } else {
            return null;
        }
    }

    /**
     * Gets a Double from the event.
     *
     * @param name
     * @return
     * @since 1.2.8
     */
    @Nullable
    public Double getDouble(String name) {
        Object i = args.get(name);
        if (i instanceof Double) {
            return (Double) i;
        } else {
            return null;
        }
    }

    /**
     * Gets a Boolean from the event.
     *
     * @param name
     * @return
     * @since 1.2.8
     */
    @Nullable
    public Boolean getBoolean(String name) {
        Object i = args.get(name);
        if (i instanceof Boolean) {
            return (Boolean) i;
        } else {
            return null;
        }
    }

    /**
     * Gets an Object from the event.
     *
     * @param name
     * @return
     * @since 1.2.8
     */
    @Nullable
    public Object getObject(String name) {
        return args.get(name);
    }

    /**
     * @return map backing the event
     * @since 1.6.4
     */
    public Map<String, Object> getUnderlyingMap() {
        return args;
    }

    /**
     * registers event so you can see it in the gui
     *
     * @since 1.3.0
     */
    public void registerEvent() {
        runner.eventRegistry.addEvent(eventName);
    }

}
