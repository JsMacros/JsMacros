package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.decoration.InteractionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(InteractionEntity.class)
public interface MixinInteractionEntity2 {

    @Invoker
    float callGetInteractionWidth();

    @Invoker
    float callGetInteractionHeight();

    @Invoker
    boolean callShouldRespond();

}
