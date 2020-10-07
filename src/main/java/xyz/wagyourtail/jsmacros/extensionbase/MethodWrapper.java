package xyz.wagyourtail.jsmacros.extensionbase;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Wraps most of the important functional interfaces.
 * 
 * @author Wagyourtail
 *
 * @param <T>
 * @param <U>
 * @param <R>
 */
public abstract class MethodWrapper<T, U, R> implements Consumer<T>, BiConsumer<T, U>, Function<T, R>, BiFunction<T, U, R>, Predicate<T>, BiPredicate<T, U>, Runnable, Supplier<R> {
    
    /**
     * Makes {@link Function} and {@link BiFunction} work together.
     */
    @Override
    public <V> MethodWrapper<T, U, V> andThen(Function<? super R,? extends V> after) {
        MethodWrapper<T, U, R> self = this;
        return new MethodWrapper<T, U, V>() {

            @Override
            public void accept(T t) {
                self.accept(t);
            }

            @Override
            public void accept(T t, U u) {
                self.accept(t, u);
            }

            @Override
            public V apply(T t) {
                return after.apply(self.apply(t));
            }

            @Override
            public V apply(T t, U u) {
                return after.apply(self.apply(t, u));
            }

            @Override
            public boolean test(T t) {
                return self.test(t);
            }
            
            @Override
            public boolean test(T arg0, U arg1) {
                return self.test(arg0, arg1);
            }

            @Override
            public void run() {
                self.run();
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
