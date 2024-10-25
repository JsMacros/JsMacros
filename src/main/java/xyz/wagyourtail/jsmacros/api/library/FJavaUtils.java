package xyz.wagyourtail.jsmacros.api.library;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.*;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Library("JavaUtils")
@SuppressWarnings("unused")
public class FJavaUtils extends BaseLibrary {

    public FJavaUtils(Core<?, ?> runner) {
        super(runner);
    }

    /**
     * Creates a java {@link ArrayList}.
     *
     * @return a java ArrayList.
     * @since 1.8.4
     */
    public ArrayList<?> createArrayList() {
        return new ArrayList<>();
    }

    /**
     * Creates a java {@link ArrayList}.
     *
     * @param array the array to add to the list
     * @param <T>   the type of the array
     * @return a java ArrayList from the given array.
     * @since 1.8.4
     */
    public <T> ArrayList<T> createArrayList(T[] array) {
        return Lists.newArrayList(array);
    }

    /**
     * Creates a java {@link HashMap}.
     *
     * @return a java HashMap.
     * @since 1.8.4
     */
    public HashMap<?, ?> createHashMap() {
        return new HashMap<>();
    }

    /**
     * Creates a java {@link HashSet}.
     *
     * @return a java HashSet.
     * @since 1.8.4
     */
    public HashSet<?> createHashSet() {
        return new HashSet<>();
    }

    /**
     * Returns a {@link SplittableRandom}.
     *
     * @return a SplittableRandom.
     * @since 1.8.4
     */
    public SplittableRandom getRandom() {
        return new SplittableRandom();
    }

    /**
     * Returns {@link SplittableRandom}, initialized with the seed to get identical sequences of
     * values at all times.
     *
     * @param seed the seed
     * @return a SplittableRandom.
     * @since 1.8.4
     */
    public SplittableRandom getRandom(long seed) {
        return new SplittableRandom(seed);
    }

    /**
     * @param raw the object to wrap
     * @return the correct instance of {@link BaseHelper} for the given object if it exists and
     * {@code null} otherwise.
     * @since 1.8.4
     */
    @Nullable
    public Object getHelperFromRaw(@NotNull Object raw) {
        Objects.requireNonNull(raw, "Object cannot be null.");
        return runner.helperRegistry.wrap(raw);
    }

    /**
     * @param array the array to convert
     * @return the String representation of the given array.
     * @since 1.8.4
     */
    public String arrayToString(Object[] array) {
        return Arrays.toString(array);
    }

    /**
     * This method will convert any objects hold in the array data to Strings and should be used for
     * multidimensional arrays.
     *
     * @param array the array to convert
     * @return the String representation of the given array.
     * @since 1.8.4
     */
    public String arrayDeepToString(Object[] array) {
        return Arrays.deepToString(array);
    }

}
