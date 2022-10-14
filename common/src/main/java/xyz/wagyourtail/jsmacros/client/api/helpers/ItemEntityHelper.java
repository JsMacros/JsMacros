package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.item.EntityItem;

public class ItemEntityHelper extends EntityHelper<EntityItem> {
    public ItemEntityHelper(EntityItem e) {
        super(e);
    }

    public ItemStackHelper getContainedItemStack() {
        return new ItemStackHelper(base.getItemStack());
    }

    @Override
    public String toString() {
        return String.format("ItemEntity:{\"containedStack\": %s}", getContainedItemStack().toString());
    }
}
