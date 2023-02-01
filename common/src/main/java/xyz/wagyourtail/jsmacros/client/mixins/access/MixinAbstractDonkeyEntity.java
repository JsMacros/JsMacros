package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.passive.AbstractDonkeyEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractDonkeyEntity.class)
public interface MixinAbstractDonkeyEntity {
    @Invoker
    int invokeGetInventorySize();

}
