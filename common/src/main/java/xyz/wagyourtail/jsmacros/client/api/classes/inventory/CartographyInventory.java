package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class CartographyInventory extends Inventory<CartographyTableScreen> {

    public CartographyInventory(CartographyTableScreen inventory) {
        super(inventory);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getMapItem() {
        return getSlot(0);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getMaterial() {
        return getSlot(1);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public ItemStackHelper getOutput() {
        return getSlot(2);
    }


}
