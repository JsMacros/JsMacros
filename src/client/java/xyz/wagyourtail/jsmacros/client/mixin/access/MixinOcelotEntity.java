package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.passive.OcelotEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(OcelotEntity.class)
public interface MixinOcelotEntity {

    @Invoker
    boolean invokeIsTrusting();

}
