package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.font.TrueTypeFont;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TrueTypeFont.class)
public class MixinTrueTypeFont {
    
    @ModifyVariable(method = "getGlyph", at = @At("STORE"), ordinal = 2)
    public int modifyWidth(int w) {
        if (w == 0) return 1;
        return w;
    }
    
    @ModifyVariable(method = "getGlyph", at = @At("STORE"), ordinal = 3)
    public int modifyHeight(int h) {
        if (h == 0) return 1;
        return h;
    }
    
}
