package xyz.wagyourtail.jsmacros.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;

public interface ItemDamageCallback {
    Event<ItemDamageCallback> EVENT = EventFactory.createArrayBacked(ItemDamageCallback.class,
            (listeners) -> (stack, damage) -> {
                for (ItemDamageCallback event : listeners) {
                    event.interact(stack, damage);
                }
            });
    void interact(ItemStack stack, int damage);
}
