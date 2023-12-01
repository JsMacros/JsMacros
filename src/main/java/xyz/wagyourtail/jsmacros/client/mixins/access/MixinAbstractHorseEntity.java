package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.passive.HorseBaseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(HorseBaseEntity.class)
public interface MixinAbstractHorseEntity {

    @Invoker
    int invokeGetInventorySize();

}
