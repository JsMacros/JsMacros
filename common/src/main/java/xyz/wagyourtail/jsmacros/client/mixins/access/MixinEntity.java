package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IMixinEntity;

@Mixin(Entity.class)
public abstract class MixinEntity implements IMixinEntity {

    @Unique
    private int glowingColor = -1;

    @Unique
    private int forceGlowing = 1;


    @Shadow
    public abstract boolean isGlowingLocal();

    @Override
    public void jsmacros_setGlowingColor(int glowingColor) {
        this.glowingColor = glowingColor & 0xFFFFFF;
    }

    @Override
    public void jsmacros_resetColor() {
        glowingColor = -1;
    }

    @Inject(method = "getTeamColorValue()I", cancellable = true, at = @At("HEAD"))
    public void getTeamColorValue(CallbackInfoReturnable<Integer> ci) {
        if(glowingColor != -1) {
            ci.setReturnValue(glowingColor);
            ci.cancel();
        }
    }

    @Override
    public void jsmacros_setForceGlowing(int glowing) {
        forceGlowing = glowing;
    }

    @Inject(method = "isGlowing", at = @At("RETURN"), cancellable = true)
    public void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (forceGlowing == 0) {
            cir.setReturnValue(false);
        } else if (forceGlowing == 2) {
            cir.setReturnValue(true);
        }
    }

}
