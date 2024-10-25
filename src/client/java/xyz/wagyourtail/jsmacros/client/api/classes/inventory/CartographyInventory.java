package xyz.wagyourtail.jsmacros.client.api.classes.inventory;

import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CartographyInventory extends Inventory<CartographyTableScreen> {

    public CartographyInventory(CartographyTableScreen inventory) {
        super(inventory);
    }

    /**
     * @return the map item.
     * @since 1.8.4
     */
    public ItemStackHelper getMapItem() {
        return getSlot(0);
    }

    /**
     * @return the paper item.
     * @since 1.8.4
     */
    public ItemStackHelper getMaterial() {
        return getSlot(1);
    }

    /**
     * @return the output item.
     * @since 1.8.4
     */
    public ItemStackHelper getOutput() {
        return getSlot(2);
    }

    @Override
    public String toString() {
        return String.format("CartographyInventory:{}");
    }

}
