package xyz.wagyourtail.jsmacros.api.functions;

import org.apache.commons.lang3.StringUtils;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.api.Functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * Functions for getting and using raw java classes, methods and functions.
 * 
 * An instance of this class is passed to scripts as the {@code reflection} variable.
 * 
 * @since 1.2.3
 * 
 * @author Wagyourtail
 *
 */
public class FReflection extends Functions {
    private Map<String, Class<?>> loadedClasses = new HashMap<>();
    
    public FReflection(String libName) {
        super(libName);
    }
    
    /**
     * @see FReflection#getClass(String, String)
     * 
     * @since 1.2.3
     * 
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> getClass(String name) throws ClassNotFoundException {
        try {
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
        } catch (ClassNotFoundException e) {
            if (loadedClasses.containsKey(name)) return loadedClasses.get(name);
            else throw e;
        }
    }
    
    /**
     * 
     * Use this to specify a class with intermediary and yarn names of classes for cleaner code.
     * 
     * @since 1.2.3
     * 
     * @param name first try
     * @param name2 second try
     * @return a {@link java.lang.Class Class} reference.
     * @throws ClassNotFoundException
     */
    public Class<?> getClass(String name, String name2) throws ClassNotFoundException {
        try {
            return getClass(name);
        } catch (ClassNotFoundException e) {
            return Class.forName(name2);
        }
    }
    
    /**
     * @see FReflection#getDeclaredMethod(Class, String, String, Class...)
     * 
     * @since 1.2.3
     * 
     * @param c
     * @param name
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public Method getDeclaredMethod(Class<?> c, String name,  Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return c.getDeclaredMethod(name,  parameterTypes);
    }
    
    /**
     * Use this to specify a method with intermediary and yarn names of classes for cleaner code.
     * 
     * @since 1.2.3
     * 
     * @param c
     * @param name
     * @param name2
     * @param parameterTypes
     * @return a {@link java.lang.reflect.Method Method} reference.
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public Method getDeclaredMethod(Class<?> c, String name, String name2,  Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        try {
            return c.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            return c.getDeclaredMethod(name2, parameterTypes);
        }
    }
    
    /**
     * @see FReflection#getDeclaredField(Class, String, String)
     * 
     * @since 1.2.3
     * 
     * @param c
     * @param name
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public Field getDeclaredField(Class<?> c, String name) throws NoSuchFieldException, SecurityException {
        return c.getDeclaredField(name);
    }
    
    /**
     * Use this to specify a field with intermediary and yarn names of classes for cleaner code.
     * 
     * @since 1.2.3
     * 
     * @param c
     * @param name
     * @param name2
     * @return a {@link java.lang.reflect.Field Field} reference.
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public Field getDeclaredField(Class<?> c, String name, String name2) throws NoSuchFieldException, SecurityException {
        try {
            return c.getDeclaredField(name);
        } catch (NoSuchFieldException | SecurityException e) {
            return c.getDeclaredField(name2);
        }
    }
    
    /**
     * Invoke a method on an object (can be {@code null}) with auto type coercion for numbers.
     * 
     * @since 1.2.3
     * 
     * @param m
     * @param c
     * @param objects
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public Object invokeMethod(Method m, Object c, Object...objects) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] params = m.getParameterTypes();
        for (int i = 0; i < objects.length; ++i) {
            objects[i] = tryAutoCastNumber(params[i], objects[i]);
        }
        return m.invoke(c, objects);
    }

    /**
     * Attempts to create a new instance of a class.
     * You probably don't have to use this one and can just call {@code new} on a {@link java.lang.Class Class}
     * unless you're in LUA, but then you also have the (kinda poorly doccumented, can someone find a better docs link for me) 
     * <a href= "http://luaj.sourceforge.net/api/3.2/org/luaj/vm2/lib/jse/LuajavaLib.html">LuaJava Library</a>.
     * 
     * @since 1.2.7
     * @param c
     * @param objects
     * @return
     */
    public Object newInstance(Class<?> c, Object...objects) {
        Class<?>[] params = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; ++i) {
            params[i] = objects[i].getClass();
        }
        try {
            Constructor<?> con = c.getConstructor(params);
            return con.newInstance(objects);
        } catch (Exception e) {
            for (Constructor<?> con : c.getConstructors()) {
                if (con.getParameterTypes().length != objects.length) continue;
                params = con.getParameterTypes();
                Object[] tempObjects = new Object[objects.length];
                try {
                    for (int i = 0; i < objects.length; ++i) {
                        tempObjects[i] = tryAutoCastNumber(params[i], objects[i]);
                    }
                    return con.newInstance(objects);
                } catch (Exception ex) {}
            }
        }
        throw new RuntimeException("Failed to create new instance, bad parameters?");
    }
    
    /**
     * Loads a jar file to be accessible with this library.
     * 
     * @since 1.2.6
     * 
     * @param file relative to the macro folder.
     * @throws IOException
     */
    public void loadJarFile(String file) throws IOException {
        File jarFile = new File(jsMacros.config.macroFolder, file);
        if (!jarFile.exists()) throw new FileNotFoundException("Jar File Not Found");
        try (URLClassLoader loader = new URLClassLoader(new URL[] {new URL("jar:file:"+jarFile.getCanonicalPath() + "!/")})) {
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while(entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class")) continue;
                    String className = StringUtils.removeEndIgnoreCase(entry.getName().replaceAll("/", "."), ".class");
                    try {
                        loadedClasses.put(className, loader.loadClass(className));
                    } catch (ClassNotFoundException e) {
                    }
                }
            } catch (IOException e) {
                throw e;
            }
        } catch (IOException e) {
            throw e;
        }
    }
    
    protected Object tryAutoCastNumber(Class<?> returnType, Object number) {
        if ((returnType == int.class || returnType == Integer.class) && !(number instanceof Integer)) {
            number = ((Number) number).intValue();
        } else if ((returnType == float.class || returnType == Float.class) && !(number instanceof Float)) {
            number = ((Number) number).floatValue();
        } else if ((returnType == double.class || returnType == Double.class) && !(number instanceof Double)) {
            number = ((Number)number).doubleValue();
        } else if ((returnType == short.class || returnType == Short.class) && !(number instanceof Short)) {
            number = ((Number)number).shortValue();
        } else if ((returnType == long.class || returnType == Long.class) && !(number instanceof Long)) {
            number = ((Number)number).longValue();
        } else if ((returnType == char.class || returnType == Character.class) && !(number instanceof Character)) {
            number = (char) ((Number)number).intValue();
        } else if ((returnType == byte.class || returnType == Byte.class) && !(number instanceof Byte)) {
            number = ((Number)number).byteValue();
        }
        return number;
    }
}
