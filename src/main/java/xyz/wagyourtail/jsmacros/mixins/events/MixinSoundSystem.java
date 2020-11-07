package xyz.wagyourtail.jsmacros.mixins.events;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.api.events.EventSound;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {
    @Inject(at = @At("HEAD"), method="play")
    public void onPlay(SoundInstance instance, CallbackInfo info) {
        String id = null;
        try {
            id = instance.getId().toString();
        } catch (NullPointerException e) {}
        float volume = 1.0F;
        float pitch = 1.0F;
        try {
            volume = instance.getVolume();
            pitch = instance.getPitch();
        } catch (NullPointerException e) {}
        
        new EventSound(id, volume, pitch, instance.getX(), instance.getY(), instance.getZ());
    }
}
