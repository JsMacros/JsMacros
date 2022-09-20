package xyz.wagyourtail.jsmacros.client.api.helpers.entity;

import net.minecraft.entity.ItemEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;

@SuppressWarnings("unused")
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
