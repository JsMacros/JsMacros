package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.boss.dragon.phase.PhaseType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(PhaseType.class)
public interface MixinPhaseType {

    @Accessor
    String getName();

}
