package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.CriterionProgress;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IAdvancementProgress;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Mixin(AdvancementProgress.class)
public abstract class MixinAdvancementProgress implements IAdvancementProgress {

    @Shadow
    private String[][] requirements;

    @Shadow
    protected abstract int countObtainedRequirements();

    @Shadow
    @Final
    Map<String, CriterionProgress> criteriaProgresses;

    @Override
    public int jsmacros_countObtainedRequirements() {
        return countObtainedRequirements();
    }

    @Override
    public String[][] jsmacros_getRequirements() {
        return requirements;
    }

    @Override
    public Map<String, CriterionProgress> jsmacros_getCriteriaProgresses() {
        return criteriaProgresses;
    }
}
