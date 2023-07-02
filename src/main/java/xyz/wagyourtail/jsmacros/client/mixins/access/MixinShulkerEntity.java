package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.mob.ShulkerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(ShulkerEntity.class)
public interface MixinShulkerEntity {

    @Invoker
    boolean invokeIsClosed();

}
