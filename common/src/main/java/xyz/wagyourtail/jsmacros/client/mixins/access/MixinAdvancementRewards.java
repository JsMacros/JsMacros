package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IAdvancementRewards;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Mixin(AdvancementRewards.class)
public class MixinAdvancementRewards implements IAdvancementRewards {
    @Shadow
    @Final
    private int experience;

    @Shadow
    @Final
    private Identifier[] loot;

    @Override
    public int jsmacros_getExperience() {
        return experience;
    }

    @Override
    public Identifier[] jsmacros_getLoot() {
        return loot;
    }
}
