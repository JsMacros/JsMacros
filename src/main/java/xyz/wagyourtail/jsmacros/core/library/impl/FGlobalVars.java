package xyz.wagyourtail.jsmacros.core.library.impl;

import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.HashMap;
import java.util.Map;

/**
 * "Global" variables for passing to other contexts.
 * 
 * An instance of this class is passed to scripts as the {@code globalvars} variable.
 * 
 * @since 1.0.4
 * 
 * @author Wagyourtail
 */
 @Library("globalvars")
public class FGlobalVars extends BaseLibrary {
    public static Map<String, Object> globalRaw = new HashMap<>();
	
	/**
	 * Put an Integer into the global variable space.
	 * 
	 * @since 1.0.4
	 * 
	 * @param name
	 * @param i
	 * @return
	 */
	public int putInt(String name, int i) {
		globalRaw.put(name, Integer.valueOf(i));
		return i;
	}
	
	/**
	 * put a String into the global variable space.
	 * 
	 * @since 1.0.4
	 * 
	 * @param name
	 * @param str
	 * @return
	 */
	public String putString(String name, String str) {
		globalRaw.put(name, str);
		return str;
	}
	
	/**
	 * put a Float into the global variable space.
	 * 
	 * @since 1.0.4
	 * 
	 * @param name
	 * @param f
	 * @return
	 */
	public float putFloat(String name, float f) {
		globalRaw.put(name, Float.valueOf(f));
		return f;
	}
	
	/**
	 * put a Double into the global variable space.
	 * 
	 * @since 1.0.8
	 * 
	 * @param name
	 * @param d
	 * @return
	 */
	public double putDouble(String name, double d) {
	    globalRaw.put(name, Double.valueOf(d));
	    return d;
	}
	
	/**
	 * put a Boolean into the global variable space.
	 * 
	 * @since 1.1.7
	 * 
	 * @param name
	 * @param b
	 * @return
	 */
	public boolean putBoolean(String name, boolean b) {
	    globalRaw.put(name, Boolean.valueOf(b));
	    return b;
	}
	
	/**
	 * put anything else into the global variable space.
	 * 
	 * @since 1.1.7
	 * 
	 * @param name
	 * @param o
	 * @return
	 */
	public Object putObject(String name, Object o) {
	    globalRaw.put(name, o);
	    return o;
	}
	
	/**
	 * Returns the type of the defined item in the global variable space as a string.
	 * 
	 * @since 1.0.4
	 * 
	 * @param name
	 * @return
	 */
	public String getType(String name) {
		Object i = globalRaw.get(name);
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
	 * Gets an Integer from the global variable space.
	 * 
	 * @since 1.0.4
	 * 
	 * @param name
	 * @return
	 */
	public Integer getInt(String name) {
		Object i = globalRaw.get(name);
		if (i instanceof Integer) {
			return (Integer) i;
		} else {
			return null;
		}
	}
	
	/**
	 * Gets a String from the global variable space
	 * 
	 * @since 1.0.4
	 * 
	 * @param name
	 * @return
	 */
	public String getString(String name) {
		Object i = globalRaw.get(name);
		if (i instanceof String) {
			return (String) i;
		} else {
			return null;
		}
	}
	
	/**
	 * Gets a Float from the global variable space.
	 * 
	 * @since 1.0.4
	 * 
	 * @param name
	 * @return
	 */
	public Float getFloat(String name) {
		Object i = globalRaw.get(name);
		if (i instanceof Float) {
			return (Float) i;
		} else {
			return null;
		}
	}
	
	/**
	 * Gets a Double from the global variable space.
	 * 
	 * @since 1.0.8
	 * 
	 * @param name
	 * @return
	 */
	public Double getDouble(String name) {
        Object i = globalRaw.get(name);
        if (i instanceof Float) {
            return (Double) i;
        } else {
            return null;
        }
    }
	
	/**
	 * Gets a Boolean from the global variable space.
	 * 
	 * @since 1.1.7
	 * 
	 * @param name
	 * @return
	 */
	public Boolean getBoolean(String name) {
        Object i = globalRaw.get(name);
        if (i instanceof Boolean) {
            return (Boolean) i;
        } else {
            return null;
        }
    }
	
	/**
	 * Gets an Object from the global variable space.
	 * 
	 * @since 1.1.7
	 * 
	 * @param name
	 * @return
	 */
	public Object getObject(String name) {
	    return globalRaw.get(name);
	}
	
	/**
	 * removes a key from the global varaible space.
	 * 
	 * @since 1.2.0
	 * 
	 * @param key
	 */
	public void remove(String key) {
	    globalRaw.remove(key);
	}
	
	public Map<String, Object> getRaw() {
		return globalRaw;
	}
}
