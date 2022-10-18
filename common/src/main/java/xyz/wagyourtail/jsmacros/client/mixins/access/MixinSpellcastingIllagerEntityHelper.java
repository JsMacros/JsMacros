package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(SpellcastingIllagerEntity.class)
public interface MixinSpellcastingIllagerEntityHelper {

    @Accessor("SPELL")
    TrackedData<Byte> getSpellKey();

}
