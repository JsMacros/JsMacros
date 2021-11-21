package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IMixinEntity;

@Mixin(Entity.class)
public abstract class MixinEntity implements IMixinEntity {

    private int glowingColor = -1;

    private boolean overrideGlowing;

    @Override
    public void setGlowingColor(int glowingColor) {
        this.glowingColor = MathHelper.clamp(glowingColor, 0, 16777215);
    }

    @Override
    public void resetColor() {
        glowingColor = -1;
    }

    @Inject(method = "getTeamColorValue()I", cancellable = true, at = @At("HEAD"))
    public void getTeamColorValue(CallbackInfoReturnable<Integer> ci) {
        if(glowingColor != -1) {
            ci.setReturnValue(glowingColor);
            ci.cancel();
        }
    }

    @Inject(method = "isGlowing()Z", cancellable = true, at = @At("HEAD"))
    public void isGlowing(CallbackInfoReturnable<Boolean> ci) {
        if(overrideGlowing) {
            ci.setReturnValue(true);
            ci.cancel();
        }
    }

    @Override
    public void setOverrideGlowing(boolean overrideGlowing) {
        this.overrideGlowing = overrideGlowing;
    }

}
