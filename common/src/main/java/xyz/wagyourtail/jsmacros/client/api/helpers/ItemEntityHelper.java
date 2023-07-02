package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.ItemEntity;

public class ItemEntityHelper extends EntityHelper<ItemEntity> {
    public ItemEntityHelper(ItemEntity e) {
        super(e);
    }

    public ItemStackHelper getContainedItemStack() {
        return new ItemStackHelper(base.getStack());
    }

    @Override
    public String toString() {
        return String.format("ItemEntity:{\"containedStack\": %s}", getContainedItemStack().toString());
    }
}
