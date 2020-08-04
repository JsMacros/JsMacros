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
            return Class.forName(name);
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
            if (params[i] == int.class) {
                objects[i] = (int)(long)objects[i];
            } else if (params[i] == float.class) {
                objects[i] = (float)(double)objects[i];
            }
            else objects[i] = params[i].cast(objects[i]);
        }
        return m.invoke(c, objects);
    }
}
