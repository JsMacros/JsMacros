package xyz.wagyourtail.jsmacros.extensionbase;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class MethodWrapper<T, U> implements Consumer<T>, BiConsumer<T, U> {

}
