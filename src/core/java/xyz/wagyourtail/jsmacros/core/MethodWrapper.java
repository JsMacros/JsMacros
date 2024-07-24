package xyz.wagyourtail.jsmacros.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

import java.util.Comparator;
import java.util.function.*;

/**
 * Wraps most of the important functional interfaces.
 *
 * @param <T>
 * @param <U>
 * @param <R>
 * @author Wagyourtail
 * @see xyz.wagyourtail.jsmacros.core.library.IFWrapper
 */
public abstract class MethodWrapper<T, U, R, C extends BaseScriptContext<?>> implements Consumer<T>, BiConsumer<T, U>, Function<T, R>, BiFunction<T, U, R>, Predicate<T>, BiPredicate<T, U>, Runnable, Supplier<R>, Comparator<T> {

    private final Object syncObject;

    /**
     * This reference will keep the context from getting garbage-collected
     * until there are no more registered method-wrappers. since the only other location
     * is at {@link Core#getContexts()} and that's a weak set.
     */
    protected final C ctx;

    protected MethodWrapper() {
        ctx = null;
        syncObject = null;
    }

    public MethodWrapper(C containingContext) {
        ctx = containingContext;
        ctx.hasMethodWrapperBeenInvoked = true;
        syncObject = ctx.getSyncObject();
    }

    @Nullable
    public C getCtx() {
        return ctx;
    }

    @Override
    public abstract void accept(T t);

    @Override
    public abstract void accept(T t, U u);

    @Override
    public abstract R apply(T t);

    @Override
    public abstract R apply(T t, U u);

    @Override
    public abstract boolean test(T t);

    @Override
    public abstract boolean test(T t, U u);

    /**
     * override to return true if the method can't join to the context it was wrapped/created in, ie for languages that don't allow multithreading.
     */
    @Deprecated
    public boolean preventSameScriptJoin() {
        return !ctx.isMultiThreaded();
    }

    /**
     * make return something to override the thread set in {@link xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros#on(String, MethodWrapper)}
     * (hi jep)
     */
    public Thread overrideThread() {
        return null;
    }

    /**
     * Makes {@link Function} and {@link BiFunction} work together.
     * Extended so it's called on every type not just those 2.
     *
     * @param after put a {@link MethodWrapper} here when using in scripts.
     */
    @NotNull
    @Override
    public <V> MethodWrapper<T, U, V, C> andThen(@NotNull Function<? super R, ? extends V> after) {
        return new AndThenMethodWrapper<>(this, after);
    }

    /**
     * Makes {@link Predicate} and {@link BiPredicate} work together
     */
    @NotNull
    @Override
    public MethodWrapper<T, U, R, C> negate() {
        return new NegateMethodWrapper<>(this);
    }

    private static class AndThenMethodWrapper<T, U, R, V, C extends BaseScriptContext<?>> extends MethodWrapper<T, U, V, C> {
        private final MethodWrapper<T, U, R, C> self;
        private final Function<? super R, ? extends V> after;

        AndThenMethodWrapper(MethodWrapper<T, U, R, C> self, Function<? super R, ? extends V> after) {
            super(self.ctx);
            this.self = self;
            this.after = after;
        }

        @Override
        public int compare(T o1, T o2) {
            int retVal = self.compare(o1, o2);
            if (after instanceof MethodWrapper) {
                ((MethodWrapper<?, ?, ?, ?>) after).run();
            }
            return retVal;
        }

        @Override
        public void accept(T t) {
            self.accept(t);
            if (after instanceof MethodWrapper) {
                ((MethodWrapper<?, ?, ?, ?>) after).run();
            }
        }

        @Override
        public void accept(T t, U u) {
            self.accept(t, u);
            if (after instanceof MethodWrapper) {
                ((MethodWrapper<?, ?, ?, ?>) after).run();
            }
        }

        @Override
        public V apply(T t) {
            return after.apply(self.apply(t));
        }

        @Override
        public V apply(T t, U u) {
            return after.apply(self.apply(t, u));
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean test(T t) {
            boolean result = self.test(t);
            if (after instanceof MethodWrapper) {
                return ((MethodWrapper<Boolean, ?, ?, ?>) after).test(result);
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean test(T arg0, U arg1) {
            boolean result = self.test(arg0, arg1);
            if (after instanceof MethodWrapper) {
                return ((MethodWrapper<Boolean, ?, ?, ?>) after).test(result);
            }
            return result;
        }

        @Override
        public boolean preventSameScriptJoin() {
            boolean afterPrevent = false;
            if (after instanceof MethodWrapper) {
                afterPrevent = ((MethodWrapper<?, ?, ?, ?>) after).preventSameScriptJoin();
            }
            return self.preventSameScriptJoin() || afterPrevent;
        }

        @Override
        public void run() {
            self.run();
            if (after instanceof MethodWrapper) {
                ((MethodWrapper<?, ?, ?, ?>) after).run();
            }
        }

        @Override
        public V get() {
            return after.apply(self.get());
        }

    }

    private static class NegateMethodWrapper<T, U, R, C extends BaseScriptContext<?>> extends MethodWrapper<T, U, R, C> {
        private final MethodWrapper<T, U, R, C> self;

        NegateMethodWrapper(MethodWrapper<T, U, R, C> self) {
            super(self.ctx);
            this.self = self;
        }

        @Override
        public int compare(T o1, T o2) {
            return -self.compare(o1, o2);
        }

        @Override
        public void accept(T t) {
            self.accept(t);
        }

        @Override
        public void accept(T t, U u) {
            self.accept(t, u);
        }

        @Override
        public R apply(T t) {
            return self.apply(t);
        }

        @Override
        public R apply(T t, U u) {
            return self.apply(t, u);
        }

        @Override
        public boolean test(T t) {
            return !self.test(t);
        }

        @Override
        public boolean test(T t, U u) {
            return !self.test(t, u);
        }

        @Override
        public boolean preventSameScriptJoin() {
            return self.preventSameScriptJoin();
        }

        @Override
        public void run() {
            self.run();
        }

        @Override
        public R get() {
            return self.get();
        }

    }

}
