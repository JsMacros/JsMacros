package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IAdvancedFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.impl.BlockFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.impl.BlockStateFilter;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.impl.StringifyFilter;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockStateHelper;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public final class WorldScannerBuilder {

    private IAdvancedFilter<BlockHelper> blockFilter;
    private IAdvancedFilter<BlockStateHelper> stateFilter;

    private FilterCategory selectedCategory;
    private Operation operation;
    private String method;

    public WorldScannerBuilder() {
        selectedCategory = FilterCategory.NONE;
        operation = Operation.NONE;
    }

    private IAdvancedFilter<?> getTargetFilter() {
        if (selectedCategory == FilterCategory.BLOCK) {
            return blockFilter;
        } else if (selectedCategory == FilterCategory.STATE) {
            return stateFilter;
        }
        return null;
    }


    private void setTargetFilter(IAdvancedFilter<?> filter) {
        if (selectedCategory == FilterCategory.BLOCK) {
            blockFilter = (IAdvancedFilter<BlockHelper>) filter;
        } else if (selectedCategory == FilterCategory.STATE) {
            stateFilter = (IAdvancedFilter<BlockStateHelper>) filter;
        }
    }

    private <T> void composeFilters(IFilter<T> filter) {
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
                        break;
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

    private void createStringFilter(String method, String... args) {
        if (selectedCategory == FilterCategory.STATE) {
            composeFilters(new StringifyFilter<BlockStateFilter>(method).addOption(args));
        } else if (selectedCategory == FilterCategory.BLOCK) {
            composeFilters(new StringifyFilter<BlockHelper>(method).addOption(args));
        }else {
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
