package xyz.wagyourtail.jsmacros.client.api.helper;

import com.google.common.collect.Iterables;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.CriterionProgress;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinAdvancementProgress;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AdvancementProgressHelper extends BaseHelper<AdvancementProgress> {

    public AdvancementProgressHelper(AdvancementProgress base) {
        super(base);
    }

    /**
     * @return {@code true} if the advancement is finished, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isDone() {
        return base.isDone();
    }

    /**
     * @return {@code true} if any criteria has already been met, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAnyObtained() {
        return base.isAnyObtained();
    }

    /**
     * @return a map of all criteria and their completion date.
     * @since 1.8.4
     */
    public Map<String, Long> getCriteria() {
        return ((MixinAdvancementProgress) base).getCriteriaProgresses().entrySet().stream().filter(e -> e.getValue().getObtainedTime() != null).collect(Collectors.toMap(
                Map.Entry::getKey,
                criterionProgressEntry -> criterionProgressEntry.getValue().getObtainedTime().toEpochMilli()
        ));
    }

    /**
     * @return all requirements of this advancement.
     * @since 1.8.4
     */
    public List<List<String>> getRequirements() {
        return ((MixinAdvancementProgress) base).getRequirements().requirements();
    }

    /**
     * @return the percentage of finished requirements.
     * @since 1.8.4
     */
    public float getPercentage() {
        return base.getProgressBarPercentage();
    }

    /**
     * @return the fraction of finished requirements to total requirements.
     * @since 1.8.4
     */
    public TextHelper getFraction() {
        return TextHelper.wrap(base.getProgressBarFraction());
    }

    /**
     * @return the amount of requirements criteria.
     * @since 1.8.4
     */
    public int countObtainedRequirements() {
        return ((MixinAdvancementProgress) base).invokeCountObtainedRequirements();
    }

    /**
     * @return the amount/values of missing criteria.
     * @since 1.8.4
     */
    public String[] getUnobtainedCriteria() {
        return Iterables.toArray(base.getUnobtainedCriteria(), String.class);
    }

    /**
     * @return the ids of the finished requirements.
     * @since 1.8.4
     */
    public String[] getObtainedCriteria() {
        return Iterables.toArray(base.getObtainedCriteria(), String.class);
    }

    /**
     * @return the earliest completion date of all criteria.
     * @since 1.8.4
     */
    public long getEarliestProgressObtainDate() {
        return base.getEarliestProgressObtainDate().toEpochMilli();
    }

    /**
     * @param criteria the criteria
     * @return the completion date of the given criteria or {@code -1} if the criteria is not met
     * yet.
     * @since 1.8.4
     */
    public long getCriterionProgress(String criteria) {
        CriterionProgress progress = base.getCriterionProgress(criteria);
        return progress == null ? -1 : progress.getObtainedTime().toEpochMilli();
    }

    /**
     * @param criteria the criteria
     * @return {@code true} if the given criteria is met, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isCriteriaObtained(String criteria) {
        return base.getCriterionProgress(criteria).isObtained();
    }

    @Override
    public String toString() {
        return String.format("AdvancementProgressHelper:{\"percent\": %f}", getPercentage());
    }

}
