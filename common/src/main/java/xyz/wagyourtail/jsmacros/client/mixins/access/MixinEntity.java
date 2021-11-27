package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IMixinEntity;

@Mixin(Entity.class)
public abstract class MixinEntity implements IMixinEntity {

    @Unique
    private int glowingColor = -1;


    @Shadow
    public abstract boolean isGlowingLocal();

    @Override
    public void setGlowingColor(int glowingColor) {
        this.glowingColor = glowingColor & 0xFFFFFF;
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

    @Redirect(method = "setGlowing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isGlowing()Z", ordinal = 0), require = 0)
    public boolean redirectIsGlowing(Entity instance) {
        return isGlowingLocal();
    }




}
