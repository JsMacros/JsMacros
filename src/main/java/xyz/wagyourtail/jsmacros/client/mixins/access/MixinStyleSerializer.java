package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Style.Serializer.class, priority = 1001)
public class MixinStyleSerializer {

    @Redirect(method = "serialize(Lnet/minecraft/text/Style;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At(value = "FIELD", target = "Lnet/minecraft/text/Style;clickEvent:Lnet/minecraft/text/ClickEvent;", opcode = Opcodes.GETFIELD))
    private ClickEvent redirectClickGetAction(Style instance) {
        ClickEvent original = instance.getClickEvent();
        if (original == null) return null;
        if (original.getAction() == null) {
            return null;
        }
        return original;
    }

}
