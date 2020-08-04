package xyz.wagyourtail.jsmacros.runscript.functions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class reflectionFunctions extends Functions {

    public reflectionFunctions(String libName) {
        super(libName);
        // TODO Auto-generated constructor stub
    }
    
    public Class<?> getClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
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

    
    
}
