package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.HashMap;

public class globalVarFunctions {
	public static HashMap<String, Object> globalRaw = new HashMap<>();
	public void putInt(String name, int i) {
		globalRaw.put(name, Integer.valueOf(i));
	}
	public void putString(String name, String str) {
		globalRaw.put(name, str);
	}
	public void putFloat(String name, float f) {
		globalRaw.put(name, Float.valueOf(f));
	}
	public void putDouble(String name, double d) {
	    globalRaw.put(name, Double.valueOf(d));
	}
	public void putBoolean(String name, boolean b) {
	    globalRaw.put(name, Boolean.valueOf(b));
	}
	public void putObject(String name, Object o) {
	    globalRaw.put(name, o);
	}
	
	public String getType(String name) {
		Object i = globalRaw.get(name);
		if (i instanceof Integer) {
			return "Int";
		} else if (i instanceof String) {
			return "String";
		} else if (i instanceof Float) {
			return "Float";
		} else if (i instanceof Double) {
            return "Double";
		} else {
			return null;
		}
	}
	
	public Integer getInt(String name) {
		Object i = globalRaw.get(name);
		if (i instanceof Integer) {
			return (Integer) i;
		} else {
			return null;
		}
	}
	public String getString(String name) {
		Object i = globalRaw.get(name);
		if (i instanceof String) {
			return (String) i;
		} else {
			return null;
		}
	}
	public Float getFloat(String name) {
		Object i = globalRaw.get(name);
		if (i instanceof Float) {
			return (Float) i;
		} else {
			return null;
		}
	}
	public Double getDouble(String name) {
        Object i = globalRaw.get(name);
        if (i instanceof Float) {
            return (Double) i;
        } else {
            return null;
        }
    }
	public Boolean getBoolean(String name) {
        Object i = globalRaw.get(name);
        if (i instanceof Boolean) {
            return (Boolean) i;
        } else {
            return null;
        }
    }
	public Object getObject(String name) {
	    return globalRaw.get(name);
	}
	
	public HashMap<String, Object> getRaw() {
		return globalRaw;
	}
}
