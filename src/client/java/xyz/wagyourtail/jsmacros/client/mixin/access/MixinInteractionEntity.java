package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.decoration.InteractionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IMixinInteractionEntity;

@Mixin(InteractionEntity.class)
public abstract class MixinInteractionEntity implements IMixinInteractionEntity {

    @Unique
    private boolean jsmacros$canHitOverride = true;

    @Override
    public void jsmacros_setCanHitOverride(boolean value) {
        jsmacros$canHitOverride = value;
    }

    @Inject(method = "canHit", at = @At("HEAD"), cancellable = true)
    public void overrideCanHit(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(jsmacros$canHitOverride);
    }

}
