package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface MixinLivingEntity {

    @Accessor
    boolean isJumping();
}
