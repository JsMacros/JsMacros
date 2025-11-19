package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import javassist.util.proxy.ProxyFactory;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.Util;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @param <T>
 * @author Wagyourtail
 * @since 1.6.0
 */
public class ProxyBuilder<T> {
    public final ProxyFactory factory;
    public final Map<MethodSigParts, MethodWrapper<ProxyReference<T>, Object[], ?, ?>> proxiedMethods = new HashMap<>();
    public final Map<String, MethodWrapper<ProxyReference<T>, Object[], ?, ?>> proxiedMethodDefaults = new HashMap<>();

    public ProxyBuilder(Class<T> clazz, Class<?>[] interfaces) {
        this.factory = new ProxyFactory();
        if (clazz.isInterface()) {
            factory.setSuperclass(Object.class);
            interfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
            interfaces[interfaces.length - 1] = clazz;
        } else {
            factory.setSuperclass(clazz);
        }
        factory.setInterfaces(interfaces);
        factory.setFilter(m -> getWrapperForMethod(m) != null);
    }

    private MethodWrapper<ProxyReference<T>, Object[], ?, ?> getWrapperForMethod(Method m) {
        MethodSigParts sig = methodToSigParts(m);
        MethodWrapper<ProxyReference<T>, Object[], ?, ?> wrapper = proxiedMethods.get(sig);
        if (wrapper == null) {
            wrapper = proxiedMethodDefaults.get(sig.name);
        }
        return wrapper;
    }

    /**
     * @param methodNameOrSig name of method or sig (the usual format)
     * @param proxyMethod
     * @return self for chaining
     * @since 1.6.0
     */
    public ProxyBuilder<T> addMethod(String methodNameOrSig, MethodWrapper<ProxyReference<T>, Object[], ?, ?> proxyMethod) throws ClassNotFoundException {
        String[] parts = methodNameOrSig.split("\\(");
        if (parts.length > 1) {
            proxiedMethods.put(mapMethodSig(methodNameOrSig), proxyMethod);
        } else {
            proxiedMethodDefaults.put(methodNameOrSig, proxyMethod);
        }
        return this;
    }

    /**
     * @param constructorArgs args for the super constructor
     * @return new instance of the constructor
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @since 1.6.0
     */
    public T buildInstance(Object[] constructorArgs) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class<?>[] params = new Class<?>[constructorArgs.length];
        for (int i = 0; i < constructorArgs.length; ++i) {
            params[i] = constructorArgs[i].getClass();
        }
        try {
            Constructor<?> con = factory.getSuperclass().getDeclaredConstructor(params);
            return buildInstance(con.getParameterTypes(), constructorArgs);
        } catch (NoSuchMethodException | SecurityException ignored) {
            for (Constructor<?> constructor : factory.getSuperclass().getDeclaredConstructors()) {
                if (areParamsCompatible(params, constructor.getParameterTypes())) {
                    return buildInstance(constructor.getParameterTypes(), constructorArgs);
                }
            }
            throw new NoSuchMethodException("Constructor for supplied types doesn't exist");
        }
    }

    /**
     * @param constructorSig  string signature (you can skip the &lt;init&gt; part)
     * @param constructorArgs args for the super constructor
     * @return new instance of the constructor
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @since 1.6.0
     */
    public T buildInstance(String constructorSig, Object[] constructorArgs) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return buildInstance(mapMethodSig(constructorSig).params, constructorArgs);
    }

    /**
     * @param constructorSig  string signature (you can skip the &lt;init&gt; part)
     * @param constructorArgs args for the super constructor
     * @return new instance of the constructor
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @since 1.6.0
     */
    public T buildInstance(Class<?>[] constructorSig, Object[] constructorArgs) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (int i = 0; i < constructorArgs.length; ++i) {
            constructorArgs[i] = Util.tryAutoCastNumber(constructorSig[i], constructorArgs[i]);
        }
        return (T) factory.create(constructorSig, constructorArgs, this::invoke);
    }

    private Object invoke(Object self, Method thisMethod, @Nullable Method proceed, Object[] args) throws Throwable {
        MethodWrapper<ProxyReference<T>, Object[], ?, ?> wrapper = getWrapperForMethod(thisMethod);
        if (wrapper == null) {
            return proceed.invoke(self, args);
        }
        if (thisMethod.getReturnType().equals(void.class)) {
            try {
                wrapper.accept(new ProxyReference<>((T) self, proceed != null ? (arg) -> {
                    try {
                        return proceed.invoke(self, arg);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                } : null), args);
            } catch (Throwable e) {
                if (Arrays.stream(thisMethod.getExceptionTypes()).anyMatch(f -> f.isAssignableFrom(e.getCause().getClass()))) {
                    throw e.getCause();
                } else if (e.getCause() instanceof RuntimeException) {
                    throw e.getCause();
                } else {
                    BaseScriptContext<?> ctx = wrapper.getCtx();
                    if (ctx != null) {
                        ctx.runner.profile.logError(e.getCause());
                    } else {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        return Util.tryAutoCastNumber(thisMethod.getReturnType(), wrapper.apply(new ProxyReference<>((T) self, proceed != null ? (arg) -> {
            try {
                return proceed.invoke(self, arg);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } : null), args));
    }

    private static final Pattern sigPart = Pattern.compile("[ZBCSIJFDV]|L(.+?);");

    private MethodSigParts mapMethodSig(String methodSig) throws ClassNotFoundException {
        String[] parts = methodSig.split("[()]", 3);
        List<Class<?>> params = new ArrayList<>();
        Matcher m = sigPart.matcher(parts[1]);
        while (m.find()) {
            String clazz = m.group(1);
            if (clazz == null) {
                params.add(getPrimitive(m.group().charAt(0)));
            } else {
                params.add(Class.forName(clazz.replace("/", ".")));
            }
        }
        Class<?> retval;
        Matcher r = sigPart.matcher(parts[2]);
        if (r.find()) {
            String clazz = r.group(1);
            if (clazz == null) {
                retval = getPrimitive(r.group().charAt(0));
            } else {
                retval = Class.forName(clazz.replace("/", "."));
            }
        } else {
            throw new IllegalArgumentException("Signature return value invalid.");
        }

        return new MethodSigParts(parts[0], params.toArray(new Class[0]), retval);
    }

    private MethodSigParts methodToSigParts(Method mthd) {
        return new MethodSigParts(mthd.getName(), mthd.getParameterTypes(), mthd.getReturnType());
    }

    private static Class<?> getPrimitive(char c) {
        return switch (c) {
            case 'Z' -> boolean.class;
            case 'B' -> byte.class;
            case 'C' -> char.class;
            case 'S' -> short.class;
            case 'I' -> int.class;
            case 'J' -> long.class;
            case 'F' -> float.class;
            case 'D' -> double.class;
            case 'V' -> void.class;
            default -> throw new NullPointerException("Unknown Primitive: " + c);
        };
    }

    private static Class<?> boxPrimitive(Class<?> primitive) {
        if (!primitive.isPrimitive()) return primitive;
        if (primitive == boolean.class) return Boolean.class;
        if (primitive == byte.class) return Byte.class;
        if (primitive == char.class) return Character.class;
        if (primitive == short.class) return Short.class;
        if (primitive == int.class) return Integer.class;
        if (primitive == long.class) return Long.class;
        if (primitive == float.class) return Float.class;
        if (primitive == double.class) return Double.class;
        if (primitive == void.class) return Void.class;
        throw new NullPointerException("Unknown Primitive: " + primitive);
    }

    private static boolean areParamsCompatible(Class<?>[] fuzzable, Class<?>[] target) {
        if (fuzzable.length != target.length) {
            return false;
        }
        for (int i = 0; i < fuzzable.length; ++i) {
            Class<?> targetArg = boxPrimitive(target[i]);
            Class<?> fuzzableArg = boxPrimitive(fuzzable[i]);
            if (targetArg.isAssignableFrom(fuzzableArg) || (Number.class.isAssignableFrom(targetArg) && Number.class.isAssignableFrom(fuzzableArg))) {
                continue;
            }
            return false;
        }
        return true;
    }

    public static class ProxyReference<T> {
        /**
         * "this" value, but like python because "this" is a keyword in java...
         */
        public final T self;
        /**
         * "super" value, but that's also a keyword so...
         */
        public final Function<Object[], Object> parent;

        public ProxyReference(T self, Function<Object[], Object> parent) {
            this.self = self;
            this.parent = parent;
        }

    }

    private static class MethodSigParts {
        public final String name;
        public final Class<?>[] params;
        public final Class<?> returnType;

        MethodSigParts(String name, Class<?>[] params, Class<?> returnType) {
            this.name = name;
            this.params = params;
            this.returnType = returnType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MethodSigParts)) {
                return false;
            }
            MethodSigParts that = (MethodSigParts) o;
            return name.equals(that.name) && Arrays.equals(params, that.params) && returnType.equals(that.returnType);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(name, returnType);
            result = 31 * result + Arrays.hashCode(params);
            return result;
        }

    }

}
