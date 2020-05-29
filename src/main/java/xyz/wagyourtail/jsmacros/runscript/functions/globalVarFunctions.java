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
	
	public String getType(String name) {
		Object i = globalRaw.get(name);
		if (i instanceof Integer) {
			return "Int";
		} else if (i instanceof String) {
			return "String";
		} else if (i instanceof Float) {
			return "Float";
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
	
	public HashMap<String, Object> getRaw() {
		return globalRaw;
	}
}
