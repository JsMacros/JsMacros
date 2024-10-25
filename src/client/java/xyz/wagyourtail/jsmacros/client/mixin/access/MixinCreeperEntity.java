package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.mob.CreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(CreeperEntity.class)
public interface MixinCreeperEntity {

    @Accessor("currentFuseTime")
    int getFuseTime();

    @Accessor("fuseTime")
    int getMaxFuseTime();

}
