package xyz.wagyourtail.jsmacros.core.service;

import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.classes.Registrable;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 1.6.4
 */
@SuppressWarnings("unused")
@Event("Service")
public class EventService extends BaseEvent {
    public final String serviceName;

    /**
     * when this service is stopped, this is run...
     * @see EventService#unregisterOnStop(boolean, Registrable[])
     */
    @Nullable
    public MethodWrapper<Object, Object, Object, ?> stopListener;
    /**
     * @see EventService#unregisterOnStop(boolean, Registrable[])
     * @since 1.9.1
     */
    @Nullable
    public MethodWrapper<Object, Object, Object, ?> postStopListener;

    boolean offEventsOnStop = false;
    @Nullable
    Registrable<?>[] registrableList = null;
    @Nullable
    BaseScriptContext<?> ctx = null;

    public EventService(Core<?, ?> runner, String name) {
        super(runner);
        this.serviceName = name;
    }

    /**
     * Setup unregister on stop. For example, {@code event.unregisterOnStop(false, d2d);} is
     * the equivalent of {@code event.stopListener = JavaWrapper.methodToJava(() => d2d.unregister());}.<br>
     * <br>
     * If this is called multiple times, the previous ones would be discarded.<br>
     * <br>
     * The order of execution is run stopListener -> off events -> unregister stuff -> run postStopListener.<br>
     * <br>
     * If anything was set to unregister, the service won't stop by itself even if it reaches the end.
     * @param offEvents whether the service manager should clear event listeners that the callback doesn't belong to this context.
     * @param list the list of registrable, such as Draw2D, Draw3D and CommandBuilder.
     * @since 1.9.1
     */
    public void unregisterOnStop(boolean offEvents, Registrable<?> ...list) {
        offEventsOnStop = offEvents;
        registrableList = list.length > 0 ? list : null;

        if (ctx != null) {
            ServiceManager.setAutoUnregisterKeepAlive(ctx, offEvents || registrableList != null);
        }
    }

    @Override
    public String toString() {
        return "EventService{" +
                "serviceName:\"" + serviceName + '\"' +
                '}';
    }

    protected Map<String, Object> args = new ConcurrentHashMap<>();

    /**
     * Put an Integer into the global variable space.
     *
     * @param name
     * @param i
     * @return
     * @since 1.6.5
     */
    public int putInt(String name, int i) {
        args.put(name, i);
        return i;
    }

    /**
     * put a String into the global variable space.
     *
     * @param name
     * @param str
     * @return
     * @since 1.6.5
     */
    public String putString(String name, String str) {
        args.put(name, str);
        return str;
    }

    /**
     * put a Double into the global variable space.
     *
     * @param name
     * @param d
     * @return
     * @since 1.6.5
     */
    public double putDouble(String name, double d) {
        args.put(name, d);
        return d;
    }

    /**
     * put a Boolean into the global variable space.
     *
     * @param name
     * @param b
     * @return
     * @since 1.6.5
     */
    public boolean putBoolean(String name, boolean b) {
        args.put(name, b);
        return b;
    }

    /**
     * put anything else into the global variable space.
     *
     * @param name
     * @param o
     * @return
     * @since 1.6.5
     */
    public Object putObject(String name, Object o) {
        if (runner.extensions.isGuestObject(o)) {
            throw new AssertionError("Cannot put a guest object into event");
        }
        args.put(name, o);
        return o;
    }

    /**
     * Returns the type of the defined item in the global variable space as a string.
     *
     * @param name
     * @return
     * @since 1.6.5
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
     * Gets an Integer from the global variable space.
     *
     * @param name
     * @return
     * @since 1.6.5
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
     * Gets an Integer from the global variable space. and then increment it there.
     *
     * @param name
     * @return
     * @since 1.6.5
     */
    @Nullable
    public Integer getAndIncrementInt(String name) {
        Object i = args.get(name);
        if (i instanceof Integer) {
            args.put(name, ((Integer) i) + 1);
            return (Integer) i;
        } else {
            return null;
        }
    }

    /**
     * Gets an integer from the global variable pace. and then decrement it there.
     *
     * @param name
     * @return
     * @since 1.6.5
     */
    @Nullable
    public Integer getAndDecrementInt(String name) {
        Object i = args.get(name);
        if (i instanceof Integer) {
            args.put(name, ((Integer) i) - 1);
            return (Integer) i;
        } else {
            return null;
        }
    }

    /**
     * increment an Integer in the global variable space. then return it.
     *
     * @param name
     * @return
     * @since 1.6.5
     */
    @Nullable
    public Integer incrementAndGetInt(String name) {
        Object i = args.get(name);
        if (i instanceof Integer) {
            args.put(name, i = ((Integer) i) + 1);
            return (Integer) i;
        } else {
            return null;
        }
    }

    /**
     * decrement an Integer in the global variable space. then return it.
     *
     * @param name
     * @return
     * @since 1.6.5
     */
    @Nullable
    public Integer decrementAndGetInt(String name) {
        Object i = args.get(name);
        if (i instanceof Integer) {
            args.put(name, i = ((Integer) i) - 1);
            return (Integer) i;
        } else {
            return null;
        }
    }

    /**
     * Gets a String from the global variable space
     *
     * @param name
     * @return
     * @since 1.6.5
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
     * Gets a Double from the global variable space.
     *
     * @param name
     * @return
     * @since 1.6.5
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
     * Gets a Boolean from the global variable space.
     *
     * @param name
     * @return
     * @since 1.6.5
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
     * toggles a global boolean and returns its new value
     *
     * @param name
     * @return
     * @since 1.6.5
     */
    @Nullable
    public Boolean toggleBoolean(String name) {
        Object i = args.get(name);
        if (i instanceof Boolean) {
            args.put(name, !(Boolean) i);
            return !(Boolean) i;
        } else {
            return null;
        }
    }

    /**
     * Gets an Object from the global variable space.
     *
     * @param name
     * @return
     * @since 1.6.5
     */
    @Nullable
    public Object getObject(String name) {
        return args.get(name);
    }

    /**
     * removes a key from the global variable space.
     *
     * @param key
     * @since 1.6.5
     */
    public void remove(String key) {
        args.remove(key);
    }

    public Map<String, Object> getRaw() {
        return args;
    }

}
