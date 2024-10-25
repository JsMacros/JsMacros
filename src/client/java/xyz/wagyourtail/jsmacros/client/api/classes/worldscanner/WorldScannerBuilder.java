package xyz.wagyourtail.jsmacros.client.api.classes.worldscanner;

import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.api.IAdvancedFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.api.IFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.impl.BlockFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.impl.BlockStateFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.worldscanner.filter.impl.StringifyFilter;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

/**
 * The builder can be used to create a world scanner with native java functions. This is especially useful for languages like javascript that
 * don't support multithreading, which causes streams to run sequential instead of parallel.
 * The builder has two filters for the block and the block state, which need to be configured separately.
 * If one function is not defined, it will just be ignored when building the scanner.<br>
 * The block and block state filters have to start with a 'with' command like {@link #withStateFilter(String)} or {@link #withStringBlockFilter()}.
 * This will overwrite all previous filters of the same type. To add more commands, it's possible to use commands with the prefix 'and', 'or', 'xor'.
 * The 'not' command will just negate the whole block or block state filter and doesn't need any arguments.<br>
 * <p>
 * All other commands need some arguments to work. For String functions, it's one of these functions: 'equals', 'contains', 'startsWith', 'endsWith' or 'matches'.
 * The strings to match are passed as vararg parameters (as many as needed, separated by a comma {@code is("chest", "barrel", "ore"}) and the filter acts
 * like a logical or, so only one of the arguments needs to match the criteria. It should be noted, that string functions call the toString method, so
 * comparing a block with something like "minecraft:stone" will always return false, because the toString method gives "{minecraft:stone}". For doing this
 * use either contains or the equals method with 'getId', as shown later.<br>
 * This will match any block that includes 'stone' or 'diorit' in its name:
 * <pre>
 * withStringBlockFilter().contains("stone") //create new block filter, check if it contains stone
 * .orStringBlockFilter().contains("diorit") //append new block filter with or and check if it contains diorit
 * </pre>
 * <p>
 * For non String functions, the method name must be passed when creating the filter. The names can be any method in {@link BlockStateHelper} or {@link BlockHelper}.
 * For more complex filters, use the MethodWrapper function {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FWorld#getWorldScanner(MethodWrapper, MethodWrapper)}.
 * Depending on the return type of the method, the following parameters must be passed to 'is' or 'test'. There are two methods, because 'is' is a keyword in some languages.<br>
 * <pre>
 * For any number:
 *   - is(operation, number) with operation = '>', '>=', '<', '<=', '==', '!=' and the number that should be compared to,
 *     i.e. is(">=", 8) returns true if the returned number is greater or equal to 8.
 * For any String:
 *   - is(method, string) with method = 'EQUALS', 'CONTAINS', 'STARTS_WITH', 'ENDS_WITH', 'MATCHES' and the string is the one to compare the returned value to,
 *     i.e. is("ENDS_WITH", "ore") checks if the returned string ends with ore (can be used with withBlockFilter("getId")).
 * For any Boolean:
 *   - is(val) with val either {@code true} or {@code false}
 *     i.e. is(false) returns true if the returned boolean value is false
 * </pre>
 *
 * @author Etheradon
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public final class WorldScannerBuilder {

    @Nullable
    private IAdvancedFilter<BlockHelper> blockFilter;
    @Nullable
    private IAdvancedFilter<BlockStateHelper> stateFilter;

    private FilterCategory selectedCategory;
    private Operation operation;
    private String method;

    public WorldScannerBuilder() {
        selectedCategory = FilterCategory.NONE;
        operation = Operation.NONE;
    }

    @Nullable
    private IAdvancedFilter<?> getTargetFilter() {
        if (selectedCategory == FilterCategory.BLOCK) {
            return blockFilter;
        } else if (selectedCategory == FilterCategory.STATE) {
            return stateFilter;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void setTargetFilter(@Nullable IAdvancedFilter<?> filter) {
        if (selectedCategory == FilterCategory.BLOCK) {
            blockFilter = (IAdvancedFilter<BlockHelper>) filter;
        } else if (selectedCategory == FilterCategory.STATE) {
            stateFilter = (IAdvancedFilter<BlockStateHelper>) filter;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void composeFilters(@Nullable IFilter<T> filter) {
        if (selectedCategory == null || selectedCategory == FilterCategory.NONE) {
            throw new IllegalStateException("No category for creating the new filter was specified.");
        } else {
            if (operation == Operation.NEW) {
                if (selectedCategory == FilterCategory.BLOCK) {
                    blockFilter = (IAdvancedFilter<BlockHelper>) filter;
                } else if (selectedCategory == FilterCategory.STATE) {
                    stateFilter = (IAdvancedFilter<BlockStateHelper>) filter;
                }
            } else {
                IAdvancedFilter<T> target = (IAdvancedFilter<T>) getTargetFilter();
                if (target == null) {
                    throw new IllegalStateException("Can't compose null filters.");
                }
                switch (operation) {
                    case OR:
                        setTargetFilter(target.or(filter));
                        break;
                    case AND:
                        setTargetFilter(target.and(filter));
                        break;
                    case XOR:
                        setTargetFilter(target.xor(filter));
                        break;
                    case NOT:
                        setTargetFilter(target.not());
                        break;
                    default:
                        throw new IllegalStateException("Unknown operation for combining filters");
                }
            }
            finishFilter();
        }
    }

    private boolean canCreateNewFilter() {
        return selectedCategory == FilterCategory.NONE;
    }

    private void createNewFilter(Operation operation, FilterCategory category, String method) {
        if (canCreateNewFilter()) {
            this.operation = operation;
            this.selectedCategory = category;
            this.method = method;
        } else {
            throw new IllegalStateException("Can't create a new filter, because the old one is not completed.");
        }
    }

    private void finishFilter() {
        if (selectedCategory != FilterCategory.NONE) {
            operation = Operation.NONE;
            selectedCategory = FilterCategory.NONE;
            method = "";
        } else {
            throw new IllegalStateException("Can't complete filter, because there is none.");
        }
    }

    public WorldScannerBuilder withStateFilter(String method) {
        createNewFilter(Operation.NEW, FilterCategory.STATE, method);
        return this;
    }

    public WorldScannerBuilder andStateFilter(String method) {
        createNewFilter(Operation.AND, FilterCategory.STATE, method);
        return this;
    }

    public WorldScannerBuilder orStateFilter(String method) {
        createNewFilter(Operation.OR, FilterCategory.STATE, method);
        return this;
    }

    public WorldScannerBuilder notStateFilter() {
        createNewFilter(Operation.NOT, FilterCategory.STATE, "");
        composeFilters(null);
        return this;
    }

    public WorldScannerBuilder withBlockFilter(String method) {
        createNewFilter(Operation.NEW, FilterCategory.BLOCK, method);
        return this;
    }

    public WorldScannerBuilder andBlockFilter(String method) {
        createNewFilter(Operation.AND, FilterCategory.BLOCK, method);
        return this;
    }

    public WorldScannerBuilder orBlockFilter(String method) {
        createNewFilter(Operation.OR, FilterCategory.BLOCK, method);
        return this;
    }

    public WorldScannerBuilder notBlockFilter() {
        createNewFilter(Operation.NOT, FilterCategory.BLOCK, "");
        composeFilters(null);
        return this;
    }

    public WorldScannerBuilder withStringBlockFilter() {
        createNewFilter(Operation.NEW, FilterCategory.BLOCK, "");
        return this;
    }

    public WorldScannerBuilder andStringBlockFilter() {
        createNewFilter(Operation.AND, FilterCategory.BLOCK, "");
        return this;
    }

    public WorldScannerBuilder orStringBlockFilter() {
        createNewFilter(Operation.OR, FilterCategory.BLOCK, "");
        return this;
    }

    public WorldScannerBuilder withStringStateFilter() {
        createNewFilter(Operation.NEW, FilterCategory.STATE, "");
        return this;
    }

    public WorldScannerBuilder andStringStateFilter() {
        createNewFilter(Operation.AND, FilterCategory.STATE, "");
        return this;
    }

    public WorldScannerBuilder orStringStateFilter() {
        createNewFilter(Operation.OR, FilterCategory.STATE, "");
        return this;
    }

    public WorldScannerBuilder is(Object... args) {
        return is(null, args);
    }

    public WorldScannerBuilder is(Object[] methodArgs, Object[] filterArgs) {
        if (selectedCategory == FilterCategory.STATE) {
            composeFilters(new BlockStateFilter(method, methodArgs, filterArgs));
        } else if (selectedCategory == FilterCategory.BLOCK) {
            composeFilters(new BlockFilter(method, methodArgs, filterArgs));
        } else {
            throw new IllegalStateException("Can't complete filter, because there is none.");
        }
        return this;
    }

    public WorldScannerBuilder test(Object... args) {
        return is(args);
    }

    public WorldScannerBuilder test(Object[] methodArgs, Object[] filterArgs) {
        return is(methodArgs, filterArgs);
    }

    public WorldScannerBuilder equals(String... args) {
        createStringFilter("EQUALS", args);
        return this;
    }

    public WorldScannerBuilder contains(String... args) {
        createStringFilter("CONTAINS", args);
        return this;
    }

    public WorldScannerBuilder startsWith(String... args) {
        createStringFilter("STARTS_WITH", args);
        return this;
    }

    public WorldScannerBuilder endsWith(String... args) {
        createStringFilter("ENDS_WITH", args);
        return this;
    }

    public WorldScannerBuilder matches(String... args) {
        createStringFilter("MATCHES", args);
        return this;
    }

    @SuppressWarnings("unchecked")
    private void createStringFilter(String method, String... args) {
        if (selectedCategory == FilterCategory.STATE) {
            composeFilters(new StringifyFilter<BlockStateFilter>(method).addOption(args));
        } else if (selectedCategory == FilterCategory.BLOCK) {
            composeFilters(new StringifyFilter<BlockHelper>(method).addOption(args));
        } else {
            throw new IllegalStateException("Can't create filter, because there is none.");
        }
    }

    public WorldScanner build() {
        return new WorldScanner(MinecraftClient.getInstance().world, blockFilter, stateFilter);
    }

    private enum Operation {
        NEW,
        OR,
        AND,
        NOT,
        XOR,
        NONE
    }

    private enum FilterCategory {
        BLOCK,
        STATE,
        NONE
    }

}