package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.passive.FishEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FishEntity.class)
public interface MixinFishEntity {

    @Invoker
    boolean invokeIsFromBucket();

}
