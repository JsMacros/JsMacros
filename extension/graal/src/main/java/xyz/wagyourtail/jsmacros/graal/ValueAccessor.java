package xyz.wagyourtail.jsmacros.graal;

import org.graalvm.polyglot.Value;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ValueAccessor {

    private static final MethodHandle GET_RECEIVER;

    public static Object getReceiver(Value value) {
        if (value == null) return null;
        try {
            return GET_RECEIVER.invoke(value);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    static {
        Class<?> abstractValue = Value.class.getSuperclass();
        try {
            Field f = abstractValue.getDeclaredField("receiver");
            f.setAccessible(true);
            GET_RECEIVER = MethodHandles.lookup().unreflectGetter(f);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(String.join(",", Arrays.stream(abstractValue.getDeclaredFields()).map(Field::getName).toArray(String[]::new)), e);
        }
    }
}
