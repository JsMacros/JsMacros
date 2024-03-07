package xyz.wagyourtail.jsmacros.core.library.impl;

import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * "Global" variables for passing to other contexts.
 * <p>
 * An instance of this class is passed to scripts as the {@code GlobalVars} variable.
 *
 * @author Wagyourtail
 * @since 1.0.4
 */
@Library("GlobalVars")
@SuppressWarnings("unused")
public class FGlobalVars extends BaseLibrary {
    public static Map<String, Object> globalRaw = new ConcurrentHashMap<>();

    /**
     * Put an Integer into the global variable space.
     *
     * @param name
     * @param i
     * @return
     * @since 1.0.4
     */
    public int putInt(String name, int i) {
        globalRaw.put(name, i);
        return i;
    }

    /**
     * put a String into the global variable space.
     *
     * @param name
     * @param str
     * @return
     * @since 1.0.4
     */
    public String putString(String name, String str) {
        globalRaw.put(name, str);
        return str;
    }

    /**
     * put a Double into the global variable space.
     *
     * @param name
     * @param d
     * @return
     * @since 1.0.8
     */
    public double putDouble(String name, double d) {
        globalRaw.put(name, d);
        return d;
    }

    /**
     * put a Boolean into the global variable space.
     *
     * @param name
     * @param b
     * @return
     * @since 1.1.7
     */
    public boolean putBoolean(String name, boolean b) {
        globalRaw.put(name, b);
        return b;
    }

    /**
     * put anything else into the global variable space.
     *
     * @param name
     * @param o
     * @return
     * @since 1.1.7
     */
    public Object putObject(String name, Object o) {
        if (Core.getInstance().extensions.isGuestObject(o)) {
            throw new AssertionError("Cannot put a guest object into global variables");
        }
        globalRaw.put(name, o);
        return o;
    }

    /**
     * Returns the type of the defined item in the global variable space as a string.
     *
     * @param name
     * @return
     * @since 1.0.4
     */
    @Nullable
    public String getType(String name) {
        Object i = globalRaw.get(name);
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
     * @since 1.0.4
     */
    @Nullable
    public Integer getInt(String name) {
        Object i = globalRaw.get(name);
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
        Object i = globalRaw.get(name);
        if (i instanceof Integer) {
            globalRaw.put(name, ((Integer) i) + 1);
            return (Integer) i;
        } else if (i == null) {
            globalRaw.put(name, 1);
            return 0;
        } else {
            return null;
        }
    }

    /**
     * Gets an integer from the global variable space. and then decrement it there.
     *
     * @param name
     * @return
     * @since 1.6.5
     */
    @Nullable
    public Integer getAndDecrementInt(String name) {
        Object i = globalRaw.get(name);
        if (i instanceof Integer) {
            globalRaw.put(name, ((Integer) i) - 1);
            return (Integer) i;
        } else if (i == null) {
            globalRaw.put(name, -1);
            return 0;
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
        Object i = globalRaw.get(name);
        if (i instanceof Integer) {
            globalRaw.put(name, i = ((Integer) i) + 1);
            return (Integer) i;
        } else if (i == null) {
            globalRaw.put(name, 1);
            return 1;
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
        Object i = globalRaw.get(name);
        if (i instanceof Integer) {
            globalRaw.put(name, i = ((Integer) i) - 1);
            return (Integer) i;
        } else if (i == null) {
            globalRaw.put(name, -1);
            return -1;
        } else {
            return null;
        }
    }

    /**
     * Gets a String from the global variable space
     *
     * @param name
     * @return
     * @since 1.0.4
     */
    @Nullable
    public String getString(String name) {
        Object i = globalRaw.get(name);
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
     * @since 1.0.8
     */
    @Nullable
    public Double getDouble(String name) {
        Object i = globalRaw.get(name);
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
     * @since 1.1.7
     */
    @Nullable
    public Boolean getBoolean(String name) {
        Object i = globalRaw.get(name);
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
        Object i = globalRaw.get(name);
        if (i instanceof Boolean) {
            globalRaw.put(name, !(Boolean) i);
            return !(Boolean) i;
        } else if (i == null) {
            globalRaw.put(name, true);
            return true;
        } else {
            return null;
        }
    }

    /**
     * Gets an Object from the global variable space.
     *
     * @param name
     * @return
     * @since 1.1.7
     */
    public Object getObject(String name) {
        return globalRaw.get(name);
    }

    /**
     * removes a key from the global variable space.
     *
     * @param key
     * @since 1.2.0
     */
    public void remove(String key) {
        globalRaw.remove(key);
    }

    public Map<String, Object> getRaw() {
        return globalRaw;
    }

}
