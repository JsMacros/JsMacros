package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import xyz.wagyourtail.jsmacros.events.SoundCallback;

@Mixin(SoundSystem.class)
public class jsmacros_SoundSystemMixin {
    @Inject(at = @At("HEAD"), method="play")
    public void jsmacros_play(SoundInstance instance, CallbackInfo info) {
        SoundCallback.EVENT.invoker().interact(instance.getId().toString());
    }
}
