package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDamage;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventHeal;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventEntityDamaged;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventEntityHealed;

@Mixin(EntityLivingBase.class)
public abstract class MixinLivingEntity {

    @Shadow public abstract float getMaxHealth();

    @Unique
    private float lastHealth;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onInit(CallbackInfo ci) {
        lastHealth = getMaxHealth();
    }

    @Inject(at = @At("HEAD"), method = "setHealth")
    public void onSetHealth(float health, CallbackInfo ci) {
        //fix for singleplayer worlds, when the client also has the integrated server
        if ((Object) this instanceof EntityPlayerMP) {
            return;
        }

        float difference = lastHealth - health;

        if (difference > 0) {
            if ((Object) this instanceof EntityPlayerSP) {
                new EventDamage(DamageSource.GENERIC, health, difference);
            }
            new EventEntityDamaged((Entity)(Object) this, health, difference);
        }
        else if (difference < 0) {

            difference *= -1;

            if ((Object) this instanceof EntityPlayerSP) {
                new EventHeal(DamageSource.GENERIC, health, difference);
            }
            new EventEntityHealed((Entity)(Object) this, health, difference);
        }
        lastHealth = health;
    }
}

