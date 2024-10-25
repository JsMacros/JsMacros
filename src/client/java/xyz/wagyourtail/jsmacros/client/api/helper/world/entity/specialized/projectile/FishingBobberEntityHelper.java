package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.projectile;

import net.minecraft.entity.projectile.FishingBobberEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinFishingBobberEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FishingBobberEntityHelper extends EntityHelper<FishingBobberEntity> {

    public FishingBobberEntityHelper(FishingBobberEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if a fish has been caught, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasCaughtFish() {
        return ((MixinFishingBobberEntity) base).getCaughtFish();
    }

    /**
     * When in open water the player can get treasures from fishing.
     *
     * @return {@code true} if the bobber is in open water, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isInOpenWater() {
        return base.isInOpenWater();
    }

    /**
     * @return {@code true} if the bobber has an entity hooked, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasEntityHooked() {
        return base.getHookedEntity() != null;
    }

    /**
     * @return the hooked entity, or {@code null} if there is no entity hooked.
     * @since 1.8.4
     */
    @Nullable
    public EntityHelper<?> getHookedEntity() {
        return hasEntityHooked() ? EntityHelper.create(base.getHookedEntity()) : null;
    }

}
