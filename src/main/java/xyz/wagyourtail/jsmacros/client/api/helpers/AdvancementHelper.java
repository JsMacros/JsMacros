package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.advancement.PlacedAdvancement;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AdvancementHelper extends BaseHelper<PlacedAdvancement> {

    public AdvancementHelper(PlacedAdvancement base) {
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
        return base.getAdvancement().requirements().requirements();
    }

    /**
     * @return the amount of requirements.
     * @since 1.8.4
     */
    public int getRequirementCount() {
        return base.getAdvancement().requirements().getLength();
    }

    /**
     * @return the identifier of this advancement.
     * @since 1.8.4
     */
    @DocletReplaceReturn("AdvancementId")
    public String getId() {
        return base.getAdvancementEntry().id().toString();
    }

    /**
     * @return the experience awarded by this advancement.
     * @since 1.8.4
     */
    public int getExperience() {
        return ((MixinAdvancementRewards) base.getAdvancement().rewards()).getExperience();
    }

    /**
     * @return the loot table ids for this advancement's rewards.
     * @since 1.8.4
     */
    public String[] getLoot() {
        return Arrays.stream(((MixinAdvancementRewards) base.getAdvancement().rewards()).getLoot()).map(Identifier::toString).toArray(String[]::new);
    }

    /**
     * @return the recipes unlocked through this advancement.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaArray<RecipeId>")
    public String[] getRecipes() {
        return (String[]) Arrays.stream(base.getAdvancement().rewards().getRecipes()).map(Identifier::toString).toArray();
    }

    /**
     * @return the progress.
     * @since 1.8.4
     */
    public AdvancementProgressHelper getProgress() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        return new AdvancementProgressHelper(((MixinClientAdvancementManager) player.networkHandler.getAdvancementHandler()).getAdvancementProgresses().get(base.getAdvancementEntry()));
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
