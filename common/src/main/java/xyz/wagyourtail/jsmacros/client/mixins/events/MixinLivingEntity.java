package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventEntityDamaged;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
    @Shadow public abstract float getHealth();

    @Shadow protected abstract int computeFallDamage(float fallDistance, float damageMultiplier);

    @Inject(at = @At("TAIL"), method = "applyDamage")
    public void onDamage(DamageSource source, float amount, CallbackInfo ci) {
        new EventEntityDamaged((Entity)(Object) this, amount);
    }

}
