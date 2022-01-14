package xyz.wagyourtail.jsmacros.client.api.classes.filter;

import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.compare.BooleanCompareFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.compare.CharCompareFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.compare.NumberCompareFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.compare.StringCompareFilter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public abstract class ClassWrapperFilter<T> extends BasicFilter<T> {
    
    protected final String methodName;
    protected Method method;
    
    protected IFilter<?> filter;
    
    //TODO: Add a way to filter non primitive objects
    
    public ClassWrapperFilter(String methodName, Map<String, Method> methods, Object... args) {
        this.methodName = methodName;
        method = methods.get(methodName);
        createFilter(args);
    }

    private void createFilter(Object[] args) {
        Class<?> returnType = method.getReturnType();
        if (returnType == boolean.class || returnType == Boolean.class) {
            filter = new BooleanCompareFilter((boolean) args[0]);
        } else if (returnType == String.class) {
            filter = new StringCompareFilter((String) args[0], (String) args[1]);
        } else if (returnType == char.class || returnType == Character.class) {
            filter = new CharCompareFilter((char) args[0]);
        } else if (returnType == Number.class 
                || returnType == int.class || returnType == Integer.class 
                || returnType == float.class || returnType == Float.class 
                || returnType == double.class || returnType == Double.class 
                || returnType == short.class || returnType == Short.class 
                || returnType == long.class || returnType == Long.class 
                || returnType == byte.class || returnType == Byte.class) {
            filter = new NumberCompareFilter((String) args[0], (Number) args[1], (String) args[2]);
        } else {
            throw new IllegalArgumentException("Methods that return objects besides String are currently not supported");
        }
    }

    @Override
    public Boolean apply(T t) {
        try {
            return ((IFilter<Object>) filter).apply(method.invoke(t, null));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected static Map<String, Method> getPublicMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getParameterCount() == 0)
                .collect(Collectors.toMap(Method::getName, p -> p));
    }
    
}
