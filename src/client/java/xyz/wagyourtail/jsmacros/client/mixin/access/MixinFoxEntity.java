package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(FoxEntity.class)
public interface MixinFoxEntity {

    @Invoker
    boolean invokeIsAggressive();

    @Invoker
    Stream<LazyEntityReference<LivingEntity>> invokeGetTrustedEntities();

    @Invoker
    boolean invokeCanTrust(LivingEntity entity);

}
