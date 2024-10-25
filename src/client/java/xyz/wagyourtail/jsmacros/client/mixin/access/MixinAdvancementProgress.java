package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.criterion.CriterionProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AdvancementProgress.class)
public interface MixinAdvancementProgress {

    @Accessor
    AdvancementRequirements getRequirements();

    @Invoker
    int invokeCountObtainedRequirements();

    @Accessor
    Map<String, CriterionProgress> getCriteriaProgresses();

}
