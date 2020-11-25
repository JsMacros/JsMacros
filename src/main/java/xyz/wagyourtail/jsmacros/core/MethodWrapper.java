package xyz.wagyourtail.jsmacros.core;

import java.util.Comparator;
import java.util.function.*;

/**
 * Wraps most of the important functional interfaces.
 * 
 * @author Wagyourtail
 *
 * @param <T>
 * @param <U>
 * @param <R>
 */
public abstract class MethodWrapper<T, U, R> implements Consumer<T>, BiConsumer<T, U>, Function<T, R>, BiFunction<T, U, R>, Predicate<T>, BiPredicate<T, U>, Runnable, Supplier<R>, Comparator<T> {
    
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
     * Makes {@link Function} and {@link BiFunction} work together.
     * Extended so it's called on every type not just those 2.
     * @param after put a {@link MethodWrapper} here when using in scripts.
     */
    @Override
    public <V> MethodWrapper<T, U, V> andThen(Function<? super R,? extends V> after) {
        MethodWrapper<T, U, R> self = this;
        return new MethodWrapper<T, U, V>() {

            @Override
            public int compare(T o1, T o2) {
                int retVal = self.compare(o1, o2);
                if (after instanceof MethodWrapper)
                    ((MethodWrapper<?, ?, ?>)after).run();
                return retVal;
            }

            @Override
            public void accept(T t) {
                self.accept(t);
                if (after instanceof MethodWrapper)
                    ((MethodWrapper<?, ?, ?>)after).run();
            }

            @Override
            public void accept(T t, U u) {
                self.accept(t, u);
                if (after instanceof MethodWrapper)
                    ((MethodWrapper<?, ?, ?>)after).run();
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
                    return ((MethodWrapper<Boolean, ?, ?>) after).test(result);
                }
                return result;
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public boolean test(T arg0, U arg1) {
                boolean result =  self.test(arg0, arg1);
                if (after instanceof MethodWrapper) {
                    return ((MethodWrapper<Boolean, ?, ?>) after).test(result);
                }
                return result;
            }

            @Override
            public void run() {
                self.run();
                if (after instanceof MethodWrapper)
                    ((MethodWrapper<?, ?, ?>)after).run();
            }

            @Override
            public V get() {
                return after.apply(self.get());
            }
            
        };
    }

    /**
     * Makes {@link Predicate} and {@link BiPredicate} work together
     */
    @Override
    public MethodWrapper<T, U, R> negate() {
        MethodWrapper<T, U, R> self = this;
        return new MethodWrapper<T, U, R>() {

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
            public void run() {
                self.run();
            }

            @Override
            public R get() {
                return self.get();
            }
            
        };
    }
}
