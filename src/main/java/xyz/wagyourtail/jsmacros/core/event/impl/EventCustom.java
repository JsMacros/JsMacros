package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom Events
 * @author Wagyourtail
 * @since 1.2.8
 */
 @Event("Custom")
public class EventCustom implements BaseEvent {
    protected Map<String, Object> args = new HashMap<>();
    public String eventName;
    
    /**
     * @param eventName name of the event. please don't use an existing one... your scripts might not like that.
     */
    public EventCustom(String eventName) {
        this.eventName = eventName;
    }
    
    /**
     * Triggers the event.
     *
     * @since 1.2.8
     */
    public void trigger() {
        profile.triggerEventNoAnything(this);
    }
    
    /**
     * trigger the event listeners, then run {@code callback} when they finish.
     * @since 1.3.1
     * @param callback used as a {@Link Runnable}, so no args, no return value.
     */
    public void trigger(MethodWrapper<Object, Object, Object> callback) {
        Thread t = new Thread(() -> {
            profile.triggerEventJoinNoAnything(this);
            callback.run();
        });
        t.start();
    }
    
    /**
     * Triggers the event and waits for it to complete.
     *
     * @since 1.2.8
     */
    public void triggerJoin() {
        profile.triggerEventJoinNoAnything(this);
    }
    
    /**
     * Put an Integer into the event.
     *
     * @param name
     * @param i
     *
     * @return
     *
     * @since 1.2.8
     */
    public int putInt(String name, int i) {
        args.put(name, Integer.valueOf(i));
        return i;
    }
    
    /**
     * put a String into the event.
     *
     * @param name
     * @param str
     *
     * @return
     *
     * @since 1.2.8
     */
    public String putString(String name, String str) {
        args.put(name, str);
        return str;
    }
    
    /**
     * put a Float into the event.
     *
     * @param name
     * @param f
     *
     * @return
     *
     * @since 1.2.8
     */
    public float putFloat(String name, float f) {
        args.put(name, Float.valueOf(f));
        return f;
    }
    
    /**
     * put a Double into the event.
     *
     * @param name
     * @param d
     *
     * @return
     *
     * @since 1.2.8
     */
    public double putDouble(String name, double d) {
        args.put(name, Double.valueOf(d));
        return d;
    }
    
    /**
     * put a Boolean into the event.
     *
     * @param name
     * @param b
     *
     * @return
     *
     * @since 1.2.8
     */
    public boolean putBoolean(String name, boolean b) {
        args.put(name, Boolean.valueOf(b));
        return b;
    }
    
    /**
     * put anything else into the event.
     *
     * @param name
     * @param o
     *
     * @return
     *
     * @since 1.2.8
     */
    public Object putObject(String name, Object o) {
        args.put(name, o);
        return o;
    }
    
    /**
     * Returns the type of the defined item in the event as a string.
     *
     * @param name
     *
     * @return
     *
     * @since 1.2.8
     */
    public String getType(String name) {
        Object i = args.get(name);
        if (i == null) {
            return null;
        } else if (i instanceof Integer) {
            return "Int";
        } else if (i instanceof String) {
            return "String";
        } else if (i instanceof Float) {
            return "Float";
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
     *
     * @return
     *
     * @since 1.2.8
     */
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
     *
     * @return
     *
     * @since 1.2.8
     */
    public String getString(String name) {
        Object i = args.get(name);
        if (i instanceof String) {
            return (String) i;
        } else {
            return null;
        }
    }
    
    /**
     * Gets a Float from the event.
     *
     * @param name
     *
     * @return
     *
     * @since 1.2.8
     */
    public Float getFloat(String name) {
        Object i = args.get(name);
        if (i instanceof Float) {
            return (Float) i;
        } else {
            return null;
        }
    }
    
    /**
     * Gets a Double from the event.
     *
     * @param name
     *
     * @return
     *
     * @since 1.2.8
     */
    public Double getDouble(String name) {
        Object i = args.get(name);
        if (i instanceof Float) {
            return (Double) i;
        } else {
            return null;
        }
    }
    
    /**
     * Gets a Boolean from the event.
     *
     * @param name
     *
     * @return
     *
     * @since 1.2.8
     */
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
     *
     * @return
     *
     * @since 1.2.8
     */
    public Object getObject(String name) {
        return args.get(name);
    }
    
    /**
     * registers event so you can see it in the gui
     * @since 1.3.0
     */
    public void registerEvent() {
        Core.instance.eventRegistry.addEvent(eventName);
    }
}
