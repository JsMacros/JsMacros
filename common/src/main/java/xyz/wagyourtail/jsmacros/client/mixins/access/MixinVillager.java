package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerEntity.class)
public interface MixinVillager {
    @Accessor
    int getCareerLevel();
}
