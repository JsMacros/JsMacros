package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity;

import net.minecraft.entity.ItemEntity;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;

@SuppressWarnings("unused")
public class ItemEntityHelper extends EntityHelper<ItemEntity> {
    public ItemEntityHelper(ItemEntity e) {
        super(e);
    }

    public ItemStackHelper getContainedItemStack() {
        return new ItemStackHelper(base.getItemStack());
    }

    @Override
    public String toString() {
        return String.format("ItemEntityHelper:{\"containedStack\": %s}", getContainedItemStack().toString());
    }
}
