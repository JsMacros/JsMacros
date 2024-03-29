package xyz.wagyourtail.jsmacros.client.mixins.access;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Style.Codecs.class, priority = 1001)
public class MixinStyleSerializer {

    @ModifyExpressionValue(method = "method_54215", at = @At(value = "FIELD", target = "Lnet/minecraft/text/Style;clickEvent:Lnet/minecraft/text/ClickEvent;", opcode = Opcodes.GETFIELD))
    private static ClickEvent redirectClickGetAction(ClickEvent original) {
        if (original == null) return null;
        if (original.getAction() == null) {
            return null;
        }
        return original;
    }

}
