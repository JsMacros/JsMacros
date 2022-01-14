package xyz.wagyourtail.jsmacros.client.api.classes.filter.impl;

import xyz.wagyourtail.jsmacros.client.api.classes.filter.ClassWrapperFilter;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockHelper;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class BlockFilter extends ClassWrapperFilter<BlockHelper> {
    
    private static final Map<String, Method> METHOD_LOOKUP = getPublicMethods(BlockHelper.class);
    
    public BlockFilter(String methodName, Object... args) {
        super(methodName, METHOD_LOOKUP, args);
    }
    
}
