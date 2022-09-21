package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.advancement.criterion.CriterionProgress;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface IAdvancementProgress {

    int jsmacros_countObtainedRequirements();

    String[][] jsmacros_getRequirements();

    Map<String, CriterionProgress> jsmacros_getCriteriaProgresses();

}
