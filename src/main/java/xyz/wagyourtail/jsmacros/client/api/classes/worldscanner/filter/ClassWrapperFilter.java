package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter;

import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.api.IFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.compare.BooleanCompareFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.compare.CharCompareFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.compare.NumberCompareFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.compare.StringCompareFilter;

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
    protected final Method method;
    protected final Object[] methodArgs;

    protected IFilter<?> filter;

    //TODO: Add a way to filter objects

    /**
     * @param methodName
     * @param methods
     * @param methodArgs the arguments that will be passed, when the specified method is invoked on the object
     * @param filterArgs the arguments for the filter
     */
    protected ClassWrapperFilter(String methodName, Map<String, Method> methods, Object[] methodArgs, Object[] filterArgs) {
        this.methodName = methodName;
        this.method = methods.get(methodName);
        this.methodArgs = methodArgs;
        this.filter = getFilter(method.getReturnType(), filterArgs);
    }

    public static IFilter<?> getFilter(Class<?> clazz, String methodName, Object... args) {
        return getFilter(getPublicNoParameterMethods(clazz).get(methodName).getReturnType(), args);
    }

    private static IFilter<?> getFilter(Class<?> returnType, Object... args) {
        if (returnType == boolean.class || returnType == Boolean.class) {
            return new BooleanCompareFilter((boolean) args[0]);
        } else if (returnType == String.class) {
            return new StringCompareFilter((String) args[0], (String) args[1]);
        } else if (returnType == char.class || returnType == Character.class) {
            return new CharCompareFilter((char) args[0]);
        } else if (returnType == int.class || returnType == Integer.class
                || returnType == float.class || returnType == Float.class
                || returnType == double.class || returnType == Double.class
                || returnType == short.class || returnType == Short.class
                || returnType == long.class || returnType == Long.class
                || returnType == byte.class || returnType == Byte.class) {
            return new NumberCompareFilter((String) args[0], args[1]);
        } else {
            throw new IllegalArgumentException("Methods that return objects besides String are currently not supported");
        }
    }

    @Override
    public Boolean apply(T t) {
        try {
            return ((IFilter<Object>) filter).apply(method.invoke(t, methodArgs));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected static Map<String, Method> getPublicNoParameterMethods(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getParameterCount() == 0)
                .collect(Collectors.toMap(Method::getName, p -> p));
    }

}
