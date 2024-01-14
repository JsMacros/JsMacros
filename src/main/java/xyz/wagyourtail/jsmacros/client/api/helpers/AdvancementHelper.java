package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinAdvancementRewards;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinClientAdvancementManager;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AdvancementHelper extends BaseHelper<Advancement> {

    public AdvancementHelper(Advancement base) {
        super(base);
    }

    /**
     * @return the parent advancement or {@code null} if there is none.
     * @since 1.8.4
     */
    @Nullable
    public AdvancementHelper getParent() {
        return base.getParent() == null ? null : new AdvancementHelper(base.getParent());
    }

    /**
     * @return a list of all child advancements.
     * @since 1.8.4
     */
    public List<AdvancementHelper> getChildren() {
        return StreamSupport.stream(base.getChildren().spliterator(), false).map(AdvancementHelper::new).collect(Collectors.toList());
    }

    /**
     * @return the requirements of this advancement.
     * @since 1.8.4
     */
    public String[][] getRequirements() {
        return base.getRequirements();
    }

    /**
     * @return the amount of requirements.
     * @since 1.8.4
     */
    public int getRequirementCount() {
        return base.getRequirementCount();
    }

    /**
     * @return the identifier of this advancement.
     * @since 1.8.4
     */
    @DocletReplaceReturn("AdvancementId")
    public String getId() {
        return base.getId().toString();
    }

    /**
     * @return a map of all criteria and their criterion of this advancement.
     * @since 1.8.4
     */
    public Map<String, String> getCriteria() {
        return base.getCriteria().entrySet().stream().filter(e -> e.getValue().getConditions() != null).collect(Collectors.toMap(Map.Entry::getKey, advancementCriterionEntry -> advancementCriterionEntry.getValue().getConditions().getId().toString()));
    }

    /**
     * @return the experience awarded by this advancement.
     * @since 1.8.4
     */
    public int getExperience() {
        return ((MixinAdvancementRewards) base.getRewards()).getExperience();
    }

    /**
     * @return the loot table ids for this advancement's rewards.
     * @since 1.8.4
     */
    public String[] getLoot() {
        return Arrays.stream(((MixinAdvancementRewards) base.getRewards()).getLoot()).map(Identifier::toString).toArray(String[]::new);
    }

    /**
     * @return the recipes unlocked through this advancement.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaArray<RecipeId>")
    public String[] getRecipes() {
        return (String[]) Arrays.stream(base.getRewards().getRecipes()).map(Identifier::toString).toArray();
    }

    /**
     * @return the progress.
     * @since 1.8.4
     */
    public AdvancementProgressHelper getProgress() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        return new AdvancementProgressHelper(((MixinClientAdvancementManager) player.networkHandler.getAdvancementHandler()).getAdvancementProgresses().get(base));
    }

    /**
     * @since 1.9.0
     * @return the json string of this advancement.
     */
    public String toJson() {
        return base.getAdvancement().toJson().toString();
    }

    @Override
    public String toString() {
        return String.format("AdvancementHelper:{\"id\": \"%s\"}", getId());
    }

}
