package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.passive.AllayEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AllayEntity.class)
public interface MixinAllayEntity {

    @Invoker
    boolean invokeCanDuplicate();

}
