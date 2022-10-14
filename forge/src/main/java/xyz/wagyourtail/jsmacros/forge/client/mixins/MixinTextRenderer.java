package xyz.wagyourtail.jsmacros.forge.client.mixins;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FontRenderer.class)
public abstract class MixinTextRenderer {

    @Shadow(remap = false) protected abstract void setColor(float r, float g, float b, float a);

    @Shadow protected abstract int getCharacterCountForWidth(String str, int wrapWidth);

    @Unique float j;
    @Unique float k;
    @Unique float l;
    @Unique boolean wasCustomColor = false;

    @Inject(method = "func_78255_a", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false, ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    public void addCustomColors(String text, boolean shadow, CallbackInfo ci, int i) {
        if (text.charAt(i+1) == '#') {
            try {
                int col = Integer.parseInt(text.substring(i + 2, i + 8), 16);
                this.j = (col >> 16 & 255) / 255F;
                this.k = (col >> 8 & 255) / 255F;
                this.l = (col & 255) / 255F;
                this.wasCustomColor = true;
            } catch (NumberFormatException ignored) {}
        }
    }

    @Redirect(method = "func_78255_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;setColor(FFFF)V", ordinal = 0, remap = false))
    public void setColorModify(FontRenderer fontRenderer, float r, float g, float b, float a) {
        if (wasCustomColor) {
            this.setColor(j, k, l, a);
        } else {
            this.setColor(r, g, b, a);
        }
    }

    @ModifyVariable(method = "func_78255_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;setColor(FFFF)V", ordinal = 0, shift = At.Shift.AFTER, remap = false), ordinal = 0)
    public int modifyIndex(int i) {
        if (wasCustomColor) {
            wasCustomColor = false;
            return i + 6;
        } else {
            return i;
        }
    }

    @Unique String shiftString = "";

    @Inject(method = "getStringWidth", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1), require = 0)
    public void fixStringWidthCustomColor(String text, CallbackInfoReturnable<Integer> cir) {
        shiftString = text;
    }

    @ModifyVariable(method = "getStringWidth", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1, shift = At.Shift.AFTER), index = 4, require = 0)
    public int shiftIndexCC(int j) {
        if (j < shiftString.length() && shiftString.charAt(j) == '#') {
            return j + 6;
        }
        return j;
    }

    @Unique
    boolean extraShiftInTrimString = false;

    @Inject(method = "trimToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;func_78263_a(C)I"), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    public void fixTrimString(String text, int width, boolean rightToLeft, CallbackInfoReturnable<String> cir, StringBuilder stringBuilder, int i, int j, int k, boolean flag, boolean flag1, int l, char c0) {
        if (flag && c0 == '#') {
            extraShiftInTrimString = true;
        }
    }


    @ModifyVariable(method = "trimToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;func_78263_a(C)I", shift = At.Shift.AFTER), index = 10, require = 0)
    public int shiftIndexTrimString(int index) {
        if (extraShiftInTrimString) return index + 6;
        return index;
    }

    @Inject(method = "trimToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(C)Ljava/lang/StringBuilder;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    public void addOtherChars(String text, int width, boolean rightToLeft, CallbackInfoReturnable<String> cir, StringBuilder stringBuilder, int i, int j, int k, boolean flag, boolean flag1, int l, char c0, int i1) {
        if (extraShiftInTrimString) {
            extraShiftInTrimString = false;
            stringBuilder.append(text, l - 5, l + 1);
        }
    }

    @Unique
    String internalTrimExtra = "";

    @Inject(method = "getCharacterCountForWidth", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1))
    public void internalTrimCustomColor(String text, int offset, CallbackInfoReturnable<Integer> cir) {
        internalTrimExtra = text;
    }

    @ModifyVariable(method = "getCharacterCountForWidth", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", shift = At.Shift.AFTER), ordinal = 2, require = 0)
    public int shiftInternalTrimIndex(int k) {
        if (k < internalTrimExtra.length() && internalTrimExtra.charAt(k) == '#') {
            return k + 6;
        }
        return k;
    }

    @Redirect(method = "wrapStringToWidth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;getCharacterCountForWidth(Ljava/lang/String;I)I"))
    public int sizeStringToWidthFixZero(FontRenderer renderer, String str, int wrapWidth) {
        return Math.max(getCharacterCountForWidth(str, wrapWidth), 1);
    }
}