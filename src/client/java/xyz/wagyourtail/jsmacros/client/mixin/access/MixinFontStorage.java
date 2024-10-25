package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.font.EmptyGlyphRenderer;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {

    // mojang broke ttf rendering as ttf tries to load a 0x0 image for space, which is not possible
    @Inject(method = "findGlyphRenderer", at = @At("HEAD"), cancellable = true)
    private void onGlyphRenderer(int codePoint, CallbackInfoReturnable<GlyphRenderer> cir) {
        if (codePoint == 32) {
            cir.setReturnValue(EmptyGlyphRenderer.INSTANCE);
        }
    }

}
