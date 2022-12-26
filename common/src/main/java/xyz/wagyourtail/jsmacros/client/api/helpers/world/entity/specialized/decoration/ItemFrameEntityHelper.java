package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.decoration;

import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ItemFrameEntityHelper extends EntityHelper<ItemFrameEntity> {

    public ItemFrameEntityHelper(ItemFrameEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if the item frame is glowing, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isGlowingFrame() {
        return base instanceof GlowItemFrameEntity;
    }

    /**
     * @return the rotation of the item inside this frame.
     *
     * @since 1.8.4
     */
    public int getRotation() {
        return base.getRotation();
    }

    /**
     * @return the item inside this item frame.
     *
     * @since 1.8.4
     */
    public ItemStackHelper getItem() {
        return new ItemStackHelper(base.getHeldItemStack());
    }

}
