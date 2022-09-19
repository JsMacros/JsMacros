package xyz.wagyourtail.jsmacros.client.api.helpers.advancement;

import net.minecraft.advancement.AdvancementProgress;

import com.google.common.collect.Iterables;
import xyz.wagyourtail.jsmacros.client.access.IAdvancementProgress;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class AdvancementProgressHelper extends BaseHelper<AdvancementProgress> {

    public AdvancementProgressHelper(AdvancementProgress base) {
        super(base);
    }

    /**
     * @return
     */
    public boolean isDone() {
        return base.isDone();
    }

    /**
     * @return
     */
    public boolean isAnyObtained() {
        return base.isAnyObtained();
    }

    /**
     * @return
     */
    public Map<String, Date> getCriteria() {
        return ((IAdvancementProgress) base).jsmacros_getCriteriaProgresses().entrySet().stream().filter(e -> e.getValue().getObtainedDate() != null).collect(Collectors.toMap(
                Map.Entry::getKey,
                criterionProgressEntry -> criterionProgressEntry.getValue().getObtainedDate()
        ));
    }

    /**
     * @return
     */
    public String[][] getRequirements() {
        return ((IAdvancementProgress) base).jsmacros_getRequirements();
    }

    /**
     * @return
     */
    public float getProgressBarPercentage() {
        return base.getProgressBarPercentage();
    }

    /**
     * @return
     */
    public String getProgressBarFraction() {
        return base.getProgressBarFraction();
    }

    /**
     * @return
     */
    public int countObtainedRequirements() {
        return ((IAdvancementProgress) base).jsmacros_countObtainedRequirements();
    }

    /**
     * @return
     */
    public String[] getUnobtainedCriteria() {
        return Iterables.toArray(base.getUnobtainedCriteria(), String.class);
    }

    /**
     * @return
     */
    public String[] getObtainedCriteria() {
        return Iterables.toArray(base.getObtainedCriteria(), String.class);
    }

    /**
     * @return
     */
    public Date getEarliestProgressObtainDate() {
        return base.getEarliestProgressObtainDate();
    }

    /**
     * @param criteria
     * @return
     */
    public Date getCriterionProgress(String criteria) {
        return base.getCriterionProgress(criteria).getObtainedDate();
    }

    /**
     * @param criteria
     * @return
     */
    public boolean isCriteriaObtained(String criteria) {
        return base.getCriterionProgress(criteria).isObtained();
    }

}
