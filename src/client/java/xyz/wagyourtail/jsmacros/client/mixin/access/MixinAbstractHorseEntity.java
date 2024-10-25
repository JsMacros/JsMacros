package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AbstractHorseEntity.class)
public interface MixinAbstractHorseEntity {

    @Invoker
    int invokeGetInventorySize();

}
