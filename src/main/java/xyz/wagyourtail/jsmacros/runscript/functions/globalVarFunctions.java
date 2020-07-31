package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class globalVarFunctions extends Functions {
    public static Map<String, Object> globalRaw = new HashMap<>();
	
    public globalVarFunctions(String libName) {
        super(libName);
    }
    
    public globalVarFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
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
	
	public void remove(String key) {
	    globalRaw.remove(key);
	}
	
	public Map<String, Object> getRaw() {
		return globalRaw;
	}
}
