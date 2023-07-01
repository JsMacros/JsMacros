package xyz.wagyourtail.jsmacros.core.classes;

import xyz.wagyourtail.Util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @param <T> the type of the wrapped class
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public class WrappedClassInstance<T> {
    protected final T instance;
    protected final Class<T> tClass;
    private final Set<Class<?>> inheritance = new LinkedHashSet<>();

    public WrappedClassInstance(T instance) {
        this.instance = instance;
        this.tClass = (Class<T>) instance.getClass();
    }

    public WrappedClassInstance(T instanceNullable, Class<T> tClass) {
        this.instance = instanceNullable;
        this.tClass = tClass;
    }

    protected synchronized Set<Class<?>> getInheritance() {
        if (!inheritance.isEmpty()) {
            return inheritance;
        }
        Class<?> current = tClass;
        do {
            inheritance.add(current);
            inheritance.addAll(getInterfaceInheritance(current));
        } while ((current = current.getSuperclass()) != Object.class);
        inheritance.add(Object.class);
        return inheritance;
    }

    private Set<Class<?>> getInterfaceInheritance(Class<?> interf) {
        Set<Class<?>> l = new HashSet<>();
        l.add(interf);
        l.addAll(Arrays.stream(interf.getInterfaces()).flatMap(e -> getInterfaceInheritance(e).stream()).collect(
                Collectors.toSet()));
        return l;
    }

    protected Field findField(Class<?> asClass, String fieldName) throws NoSuchFieldException, IOException {
        Field fd = asClass.getDeclaredField(fieldName);
        fd.setAccessible(true);
        return fd;
    }

    public Object getFieldValue(String fieldName) throws NoSuchFieldException, IllegalAccessException, IOException {
        Field fd = null;
        for (Class<?> cls : getInheritance()) {
            try {
                fd = findField(cls, fieldName);
                break;
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (fd == null) {
            throw new NoSuchFieldException();
        }
        return fd.get(instance);
    }

    public Object getFieldValueAsClass(String asClass, String fieldName) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> cls = getClass(asClass);
        return findField(cls, fieldName).get(instance);
    }

    public void setFieldValue(String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalAccessException, IOException {
        Field fd = null;
        for (Class<?> cls : getInheritance()) {
            try {
                fd = findField(cls, fieldName);
                break;
            } catch (NoSuchFieldException ignored) {
            }
        }
        if (fd == null) {
            throw new NoSuchFieldException();
        }
        fd.set(instance, fieldValue);
    }

    public void setFieldValueAsClass(String asClass, String fieldName, Object fieldValue) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> cls = getClass(asClass);
        findField(cls, fieldName).set(instance, fieldValue);
    }

    private final Pattern sigPart = Pattern.compile("[ZBCSIJFDV]|L(.+?);");

    private Class<?> getPrimitive(char c) {
        switch (c) {
            case 'Z':
                return boolean.class;
            case 'B':
                return byte.class;
            case 'C':
                return char.class;
            case 'S':
                return short.class;
            case 'I':
                return int.class;
            case 'J':
                return long.class;
            case 'F':
                return float.class;
            case 'D':
                return double.class;
            case 'V':
                return void.class;
            default:
                throw new NullPointerException("Unknown Primitive: " + c);
        }
    }

    protected Class<?> getClass(String className) throws ClassNotFoundException, IOException {
        return Class.forName(className.replace("/", "."));
    }

    private MethodSigParts mapMethodSig(String methodSig) throws ClassNotFoundException, IOException {
        String[] parts = methodSig.split("[()]", 3);
        List<Class<?>> params = new ArrayList<>();
        Matcher m = sigPart.matcher(parts[1]);
        while (m.find()) {
            String clazz = m.group(1);
            if (clazz == null) {
                params.add(getPrimitive(m.group().charAt(0)));
            } else {
                params.add(getClass(clazz));
            }
        }
        Class<?> retval;
        Matcher r = sigPart.matcher(parts[2]);
        if (r.find()) {
            String clazz = r.group(1);
            if (clazz == null) {
                retval = getPrimitive(r.group().charAt(0));
            } else {
                retval = getClass(clazz);
            }
        } else {
            throw new IllegalArgumentException("Signature return value invalid.");
        }
        return new MethodSigParts(parts[0], params.toArray(new Class[0]), retval);
    }

    protected Method findMethod(Class<?> asClass, String methodSig, MethodSigParts parsedMethodSig) throws IOException, NoSuchMethodException {
        Method md = asClass.getDeclaredMethod(parsedMethodSig.name, parsedMethodSig.params);
        md.setAccessible(true);
        return md;
    }

    protected Method findMethod(Class<?> asClass, String methodName, Class<?>[] fuzzableParams) throws IOException, NoSuchMethodException {
        for (Method method : asClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                Class<?>[] methodParams = method.getParameterTypes();
                if (areParamsCompatible(fuzzableParams, methodParams)) {
                    method.setAccessible(true);
                    return method;
                }
            }
        }
        throw new NoSuchMethodException();
    }

    protected boolean areParamsCompatible(Class<?>[] fuzzable, Class<?>[] target) {
        if (fuzzable.length != target.length) {
            return false;
        }
        for (int i = 0; i < fuzzable.length; ++i) {
            if (Number.class.isAssignableFrom(fuzzable[i]) && (Number.class.isAssignableFrom(target[i]) || target[i].isPrimitive())) {
                continue;
            }
            if (target[i].isAssignableFrom(fuzzable[i])) {
                continue;
            }
            return false;
        }
        return true;
    }

    public Object invokeMethod(String methodNameOrSig, Object... params) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        Method md = null;
        if (methodNameOrSig.contains("(")) {
            MethodSigParts methodSig = mapMethodSig(methodNameOrSig);
            for (Class<?> cls : getInheritance()) {
                try {
                    md = findMethod(cls, methodNameOrSig, methodSig);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
        } else {
            Class<?>[] paramClasses = Arrays.stream(params).map(Object::getClass).toArray(Class[]::new);
            for (Class<?> cls : getInheritance()) {
                try {
                    md = findMethod(cls, methodNameOrSig, paramClasses);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }
        }
        if (md == null) {
            throw new NoSuchMethodException();
        }
        Class<?>[] rightParamClasses = md.getParameterTypes();
        for (int i = 0; i < params.length; ++i) {
            params[i] = Util.tryAutoCastNumber(rightParamClasses[i], params[i]);
        }
        return md.invoke(instance, params);
    }

    public Object invokeMethodAsClass(String asClass, String methodNameOrSig, Object... params) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> cls = getClass(asClass);
        Method md;
        if (methodNameOrSig.contains("(")) {
            md = findMethod(cls, methodNameOrSig, mapMethodSig(methodNameOrSig));
        } else {
            md = findMethod(cls, methodNameOrSig, Arrays.stream(params).map(Object::getClass).toArray(Class[]::new));
        }
        Class<?>[] rightParamClasses = md.getParameterTypes();
        for (int i = 0; i < params.length; ++i) {
            params[i] = Util.tryAutoCastNumber(rightParamClasses[i], params[i]);
        }
        return md.invoke(instance, params);
    }

    /**
     * @return
     * @since 1.6.5
     */
    public T getRawInstance() {
        return instance;
    }

    /**
     * @return
     * @since 1.6.5
     */
    public Class<T> getRawClass() {
        return tClass;
    }

    public static class MethodSigParts {
        public final String name;
        public final Class<?>[] params;
        public final Class<?> returnType;

        MethodSigParts(String name, Class<?>[] params, Class<?> returnType) {
            this.name = name;
            this.params = params;
            this.returnType = returnType;
        }

    }

}
