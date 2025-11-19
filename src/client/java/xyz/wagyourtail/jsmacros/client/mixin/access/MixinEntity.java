package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IMixinEntity;

@Mixin(Entity.class)
public abstract class MixinEntity implements IMixinEntity {

    @Unique
    private int jsmacros$glowingColor = -1;

    @Unique
    private int jsmacros$forceGlowing = 1;

    @Override
    public void jsmacros_setGlowingColor(int glowingColor) {
        this.jsmacros$glowingColor = glowingColor;
    }

    @Override
    public void jsmacros_resetColor() {
        jsmacros$glowingColor = -1;
    }

    @Inject(method = "getTeamColorValue()I", cancellable = true, at = @At("HEAD"))
    public void getTeamColorValue(CallbackInfoReturnable<Integer> ci) {
        if (jsmacros$glowingColor != -1) {
            ci.setReturnValue(jsmacros$glowingColor);
            ci.cancel();
        }
    }

    @Override
    public void jsmacros_setForceGlowing(int glowing) {
        jsmacros$forceGlowing = glowing;
    }

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (jsmacros$forceGlowing == 0) {
            cir.setReturnValue(false);
        } else if (jsmacros$forceGlowing == 2) {
            cir.setReturnValue(true);
        }
    }

}
