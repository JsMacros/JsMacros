package xyz.wagyourtail.jsmacros.client.mixins.access;

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
    private int glowingColor = -1;

    @Unique
    private int forceGlowing = 1;

    @Override
    public void jsmacros_setGlowingColor(int glowingColor) {
        this.glowingColor = glowingColor & 0xFFFFFF;
    }

    @Override
    public void jsmacros_resetColor() {
        glowingColor = -1;
    }

    @Override
    public int jsmacros_getGlowingColor() {
        return this.glowingColor;
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
