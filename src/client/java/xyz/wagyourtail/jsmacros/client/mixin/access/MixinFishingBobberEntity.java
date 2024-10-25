package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(FishingBobberEntity.class)
public interface MixinFishingBobberEntity {

    @Accessor
    boolean getCaughtFish();

}
