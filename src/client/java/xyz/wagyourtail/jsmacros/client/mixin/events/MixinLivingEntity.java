package xyz.wagyourtail.jsmacros.client.mixin.events;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventDamage;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventHeal;
import xyz.wagyourtail.jsmacros.client.api.event.impl.player.EventHealthChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventEntityDamaged;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventEntityHealed;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    //IGNORE
    public MixinLivingEntity(EntityType<?> arg, World arg2) {
        super(arg, arg2);
    }

    @Shadow
    public abstract float getMaxHealth();

    @Unique
    private float jsmacros$lastHealth;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onInit(CallbackInfo ci) {
        jsmacros$lastHealth = getMaxHealth();
    }

    @Inject(at = @At("HEAD"), method = "setHealth")
    public void onSetHealth(float health, CallbackInfo ci) {
        //fix for singleplayer worlds, when the client also has the integrated server
        if ((Object) this instanceof ServerPlayerEntity) {
            return;
        }

        float difference = jsmacros$lastHealth - health;

        if (difference > 0) {
            if ((Object) this instanceof ClientPlayerEntity) {
                new EventDamage(getWorld().getDamageSources().generic(), health, difference).trigger();
                new EventHealthChange(health, -difference).trigger();
            }
            new EventEntityDamaged((Entity) (Object) this, health, difference).trigger();
        } else if (difference < 0) {

            difference *= -1;

            if ((Object) this instanceof ClientPlayerEntity) {
                new EventHeal(getWorld().getDamageSources().generic(), health, difference).trigger();
                new EventHealthChange(health, difference).trigger();
            }
            new EventEntityHealed((Entity) (Object) this, health, difference).trigger();
        }
        jsmacros$lastHealth = health;
    }

}
