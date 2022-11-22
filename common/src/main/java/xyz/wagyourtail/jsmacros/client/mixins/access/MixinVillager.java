package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.passive.EntityVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityVillager.class)
public interface MixinVillager {
    @Accessor
    int getCareerLevel();
}
