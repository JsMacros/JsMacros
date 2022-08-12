package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.font.TrueTypeFont;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(TrueTypeFont.class)
public class MixinTrueTypeFont {
    
    // cancel canceling spaces by making their calculated width/height not equal to 0 which cancels their existence in the font entirely
    // also I dont actually know which one is width and height, it doesn't matter
    
    
    @ModifyVariable(
            method = "getGlyph(I)Lnet/minecraft/client/font/TrueTypeFont$TtfGlyph;",
            at = @At("STORE"),
            ordinal = 1,
            slice = @Slice(from = @At(value = "INVOKE", target = "Lorg/lwjgl/stb/STBTruetype;stbtt_GetGlyphBitmapBoxSubpixel(Lorg/lwjgl/stb/STBTTFontinfo;IFFFFLjava/nio/IntBuffer;Ljava/nio/IntBuffer;Ljava/nio/IntBuffer;Ljava/nio/IntBuffer;)V", remap = false), to = @At("TAIL"))
    )
    public int modifyWidth(int w, int i) {
        if (i == 32) return 1;
        return w;
    }
    
    @ModifyVariable(
            method = "getGlyph(I)Lnet/minecraft/client/font/TrueTypeFont$TtfGlyph;",
            at = @At("STORE"),
            ordinal = 2,
            slice = @Slice(from = @At(value = "INVOKE", target = "Lorg/lwjgl/stb/STBTruetype;stbtt_GetGlyphBitmapBoxSubpixel(Lorg/lwjgl/stb/STBTTFontinfo;IFFFFLjava/nio/IntBuffer;Ljava/nio/IntBuffer;Ljava/nio/IntBuffer;Ljava/nio/IntBuffer;)V", remap = false), to = @At("TAIL"))
    )
    public int modifyHeight(int h, int i) {
        if (i == 32) return 1;
        return h;
    }
    
}
