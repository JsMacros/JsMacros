package xyz.wagyourtail.jsmacros.client.api.classes.filter.impl;

import xyz.wagyourtail.jsmacros.client.api.classes.filter.ClassWrapperFilter;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockStateHelper;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class BlockStateFilter extends ClassWrapperFilter<BlockStateHelper> {
    
    private static final Map<String, Method> METHOD_LOOKUP = getPublicMethods(BlockStateHelper.class);

    public BlockStateFilter(String methodName, Object... args) {
        super(methodName, METHOD_LOOKUP, args);
    }
    
}
