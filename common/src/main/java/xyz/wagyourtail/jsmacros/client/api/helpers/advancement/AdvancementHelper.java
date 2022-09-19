package xyz.wagyourtail.jsmacros.client.api.helpers.advancement;

import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

import xyz.wagyourtail.jsmacros.client.access.IAdvancementRewards;
import xyz.wagyourtail.jsmacros.client.access.IClientAdvancementManager;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class AdvancementHelper extends BaseHelper<Advancement> {

    public AdvancementHelper(Advancement base) {
        super(base);
    }

    /**
     * @return
     */
    public AdvancementHelper getParent() {
        return new AdvancementHelper(base.getParent());
    }

    /**
     * @return
     */
    public List<AdvancementHelper> getChildren() {
        return StreamSupport.stream(base.getChildren().spliterator(), false).map(AdvancementHelper::new).toList();
    }

    /**
     * @return
     */
    public String[][] getRequirements() {
        return base.getRequirements();
    }

    /**
     * @return
     */
    public int getRequirementCount() {
        return base.getRequirementCount();
    }

    /**
     * @return
     */
    public String getId() {
        return base.getId().toString();
    }

    /**
     * @return
     */
    public Map<String, String> getCriteria() {
        return base.getCriteria().entrySet().stream().filter(e -> e.getValue().getConditions() != null).collect(Collectors.toMap(
                Map.Entry::getKey,
                advancementCriterionEntry -> advancementCriterionEntry.getValue().getConditions().getId().toString()
        ));
    }

    /**
     * @return
     */
    public int getExperience() {
        return ((IAdvancementRewards) base.getRewards()).jsmacros_getExperience();
    }

    /**
     * @return
     */
    public String[] getLoot() {
        return (String[]) Arrays.stream(((IAdvancementRewards) base.getRewards()).jsmacros_getLoot()).map(Identifier::toString).toArray();
    }

    /**
     * @return
     */
    public String[] getRecipes() {
        return (String[]) Arrays.stream(base.getRewards().getRecipes()).map(Identifier::toString).toArray();
    }

    /**
     * @return
     */
    public AdvancementProgressHelper getProgress() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        return new AdvancementProgressHelper(((IClientAdvancementManager) player.networkHandler.getAdvancementHandler()).jsmacros_getAdvancementProgress().get(base));
    }

}
