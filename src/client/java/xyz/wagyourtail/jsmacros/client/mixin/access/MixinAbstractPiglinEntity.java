package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.mob.AbstractPiglinEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AbstractPiglinEntity.class)
public interface MixinAbstractPiglinEntity {

    @Invoker
    boolean invokeIsImmuneToZombification();

}
