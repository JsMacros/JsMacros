package xyz.wagyourtail.jsmacros.client.api.helpers;

import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class RecipeHelper extends BaseHelper<Object> {
    protected int syncId;
    
    public RecipeHelper(Object base, int syncId) {
        super(base);
        this.syncId = syncId;
    }
    
    /**
     * @since 1.3.1
     * @return
     */
    public String getId() {
        throw new AssertionError("Not implemented!");
    }

    /**
     * get ingredients list
     * @since 1.8.3
     * @return
     */
    public List<List<ItemStackHelper>> getIngredients() {
        throw new AssertionError("Not implemented!");
    }

    /**
     * @since 1.3.1
     * @return
     */
    public ItemStackHelper getOutput() {
        throw new AssertionError("Not implemented!");
    }
    
    /**
     * @since 1.3.1
     * @param craftAll
     */
    public void craft(boolean craftAll) {
        throw new AssertionError("Not implemented!");
    }
    
    public String toString() {
        throw new AssertionError("Not implemented!");
    }
    
}
