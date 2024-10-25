package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.passive.HorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(HorseEntity.class)
public interface MixinHorseEntity {

    @Invoker
    int invokeGetHorseVariant();

}
