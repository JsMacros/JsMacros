package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.passive.FoxEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.UUID;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(FoxEntity.class)
public interface MixinFoxEntity {

    @Invoker
    boolean invokeIsAggressive();

    @Invoker
    List<UUID> invokeGetTrustedUuids();

}
