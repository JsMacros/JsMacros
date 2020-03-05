package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;
import xyz.wagyourtail.jsmacros.events.AirChangeCallback;
import xyz.wagyourtail.jsmacros.events.DamageCallback;

@Mixin(ClientPlayerEntity.class)
class jsmacros_ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    
    @Override
    public void setAir(int air) {
        if (air % 20 == 0) AirChangeCallback.EVENT.invoker().interact(air);
        super.setAir(air);
    }
    
    @Inject(at = @At("TAIL"), method="applyDamage")
    private void jsmacros_applyDamage(DamageSource source, float amount, final CallbackInfo info) {
        DamageCallback.EVENT.invoker().interact(source, this.getHealth(), amount);
    }
    
    
    
    // IGNORE
    public jsmacros_ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
        // TODO Auto-generated constructor stub
    }
    //
}
