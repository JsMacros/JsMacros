package xyz.wagyourtail.jsmacros.runscript.functions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class reflectionFunctions extends Functions {

    public reflectionFunctions(String libName) {
        super(libName);
        // TODO Auto-generated constructor stub
    }
    
    public Class<?> getClass(String name) throws ClassNotFoundException {
        switch (name) {
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "char":
                return char.class;
            case "void":
                return void.class;
            default:
                return Class.forName(name);
        }
    }
    
    public Class<?> getClass(String name, String name2) throws ClassNotFoundException {
        try {
            return getClass(name);
        } catch (ClassNotFoundException e) {
            return Class.forName(name2);
        }
    }
    
    public Method getDeclaredMethod(Class<?> c, String name,  Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return c.getDeclaredMethod(name,  parameterTypes);
    }
    
    public Method getDeclaredMethod(Class<?> c, String name, String name2,  Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        try {
            return c.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            return c.getDeclaredMethod(name2, parameterTypes);
        }
    }
    
    public Field getDeclaredField(Class<?> c, String name) throws NoSuchFieldException, SecurityException {
        return c.getDeclaredField(name);
    }
    
    public Field getDeclaredField(Class<?> c, String name, String name2) throws NoSuchFieldException, SecurityException {
        try {
            return c.getDeclaredField(name);
        } catch (NoSuchFieldException | SecurityException e) {
            return c.getDeclaredField(name2);
        }
    }
    
    public Object invokeMethod(Method m, Object c, Object...objects) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] params = m.getParameterTypes();
        for (int i = 0; i < objects.length; ++i) {
            if ((params[i] == int.class || params[i] == Integer.class) && !(objects[i] instanceof Integer)) {
                objects[i] = ((Number)objects[i]).intValue();
            } else if ((params[i] == float.class || params[i] == Float.class) && !(objects[i] instanceof Float)) {
                objects[i] = ((Number)objects[i]).floatValue();
            } else if ((params[i] == double.class || params[i] == Double.class) && !(objects[i] instanceof Double)) {
                objects[i] = ((Number)objects[i]).doubleValue();
            } else if ((params[i] == short.class || params[i] == Short.class) && !(objects[i] instanceof Short)) {
                objects[i] = ((Number)objects[i]).shortValue();
            } else if ((params[i] == long.class || params[i] == Long.class) && !(objects[i] instanceof Long)) {
                objects[i] = ((Number)objects[i]).longValue();
            } else if ((params[i] == char.class || params[i] == Character.class) && !(objects[i] instanceof Character)) {
                objects[i] = (char) ((Number)objects[i]).intValue();
            } else if ((params[i] == byte.class || params[i] == Byte.class) && !(objects[i] instanceof Byte)) {
                objects[i] = ((Number)objects[i]).byteValue();
            }
        }
        return m.invoke(c, objects);
    }
}
