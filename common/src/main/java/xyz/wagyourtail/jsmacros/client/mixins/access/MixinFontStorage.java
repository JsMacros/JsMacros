package xyz.wagyourtail.jsmacros.client.mixins.access;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.font.*;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {
    
    @Shadow @Final private static Glyph SPACE;
    
    @Shadow @Final private Int2ObjectMap<IntList> charactersByWidth;
    
    @Shadow protected abstract RenderableGlyph getRenderableGlyph(int i);
    
    // compute proper width of ttf space
    @Inject(method = "setFonts", at = @At("TAIL"))
    private void fixSpaceCharWidth(List<Font> fonts, CallbackInfo ci) {
        if (fonts.size() == 0) return;
        for (Font f : fonts) {
            if (f instanceof TrueTypeFont) {
                Glyph space = f.getGlyph(32);
                if (space != null) {
                    charactersByWidth.getOrDefault(MathHelper.ceil(SPACE.getAdvance(false)), new IntArrayList()).rem(32);
                    this.charactersByWidth.computeIfAbsent(MathHelper.ceil(space.getAdvance(false)), (ix) -> new IntArrayList()).add(32);
                    return;
                }
            } else {
                return;
            }
        }
    }
    
    // allow ttf space to be loaded
    @Group(name = "getGlyphOF", min = 1, max = 1)
    @ModifyArg(method = "getGlyph", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;computeIfAbsent(ILit/unimi/dsi/fastutil/ints/Int2ObjectFunction;)Ljava/lang/Object;", remap = false))
    private Int2ObjectFunction<Glyph> modifyLambda(Int2ObjectFunction<Glyph> original) {
        return (ix) -> {
            Glyph g = this.getRenderableGlyph(ix);
            // null check is for below inject which makes get return null if it hits non ttf font on space char
            if (ix == 32 && g == null) return SPACE;
            return g;
        };
    }
    
    @Group(name = "getGlyphOF", min = 1, max = 1)
    @ModifyVariable(method = "getGlyph", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private Glyph redirectGlyphOF(Glyph space, int charIn) {
        Glyph g = this.getRenderableGlyph(charIn);
        // null check is for below inject which makes get return null if it hits non ttf font on space char
        if (charIn == 32 && g == null) return SPACE;
        return g;
    }
    
    // return null if space and not TTF, see above method
    @Inject(method = "getRenderableGlyph", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/Font;getGlyph(I)Lnet/minecraft/client/font/RenderableGlyph;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void nonTTFSpaceSizeFix(int i, CallbackInfoReturnable<RenderableGlyph> cir, Iterator<Font> var2, Font font) {
        if (i == 32 && !(font instanceof TrueTypeFont)) {
            cir.setReturnValue(null);
            cir.cancel();
        }
    }
}
