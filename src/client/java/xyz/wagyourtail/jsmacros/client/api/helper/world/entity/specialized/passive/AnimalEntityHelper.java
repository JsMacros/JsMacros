package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.AnimalEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AnimalEntityHelper<T extends AnimalEntity> extends MobEntityHelper<T> {

    public AnimalEntityHelper(T base) {
        super(base);
    }

    /**
     * @param item the item to check
     * @return {@code true} if the item can be used to feed and breed this animal, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isFood(ItemHelper item) {
        return base.isBreedingItem(item.getRaw().getDefaultStack());
    }

    /**
     * @param item the item to check
     * @return {@code true} if the item can be used to feed and breed this animal, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isFood(ItemStackHelper item) {
        return base.isBreedingItem(item.getRaw());
    }

    /**
     * @param other the other animal to check
     * @return {@code true} if this animal can be bred with the other animal, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean canBreedWith(AnimalEntityHelper<?> other) {
        return base.canBreedWith(other.getRaw());
    }

}
