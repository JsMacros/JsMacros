package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Style.Serializer.class, priority = 1001)
public class MixinStyleSerializer {

    @Redirect(method = "serialize(Lnet/minecraft/text/Style;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/ClickEvent$Action;getName()Ljava/lang/String;"))
    public String redirectClickGetAction(ClickEvent.Action action) {
        if (action == null) {
            return "custom";
        }
        return action.getName();
    }

}