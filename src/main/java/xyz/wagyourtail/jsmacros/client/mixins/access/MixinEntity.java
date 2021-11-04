package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow public abstract boolean isGlowingLocal();

    @Redirect(method = "setGlowing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isGlowing()Z"))
    private boolean onSetGlowing(Entity entity) {
        return isGlowingLocal();
    }
}
