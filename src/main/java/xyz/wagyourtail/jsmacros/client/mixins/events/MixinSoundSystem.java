package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventSound;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {
    @Inject(at = @At("HEAD"), method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", cancellable = true)
    public void onPlay(SoundInstance instance, CallbackInfo info) {
        String id = null;
        try {
            id = instance.getId().toString();
        } catch (NullPointerException ignored) {
        }
        float volume = 1.0F;
        float pitch = 1.0F;
        try {
            volume = instance.getVolume();
            pitch = instance.getPitch();
        } catch (NullPointerException ignored) {
        }

        EventSound ev = new EventSound(id, volume, pitch, instance.getX(), instance.getY(), instance.getZ());
        ev.trigger();
        if (ev.isCanceled()) {
            info.cancel();
        }
    }

}
