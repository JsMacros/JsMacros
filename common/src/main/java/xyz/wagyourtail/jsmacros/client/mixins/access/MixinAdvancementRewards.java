package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AdvancementRewards.class)
public interface MixinAdvancementRewards {

    @Accessor
    int getExperience();

    @Accessor
    Identifier[] getLoot();

    @Accessor
    Identifier[] getRecipes();

}
