package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import xyz.wagyourtail.jsmacros.compat.interfaces.IEntityTrackingSoundInstance;
import xyz.wagyourtail.jsmacros.events.SoundCallback;
import xyz.wagyourtail.jsmacros.reflector.EntityHelper;

@Mixin(SoundSystem.class)
public class jsmacros_SoundSystemMixin {
    @Inject(at = @At("HEAD"), method="play")
    public void jsmacros_play(SoundInstance instance, CallbackInfo info) {
        String id = null;
        try {
            id = instance.getId().toString();
        } catch (NullPointerException e) {}
        EntityHelper entity = null;
        if (instance instanceof EntityTrackingSoundInstance) {
            entity = EntityHelper.create(((IEntityTrackingSoundInstance) instance).getEntity());
        }
        float volume = 1.0F;
        float pitch = 1.0F;
        try {
            volume = instance.getVolume();
            pitch = instance.getPitch();
        } catch (NullPointerException e) {}
        
        SoundCallback.EVENT.invoker().interact(id, volume, pitch, instance.getX(), instance.getY(), instance.getZ(), entity);
    }
}
