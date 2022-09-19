package xyz.wagyourtail.jsmacros.client.api.helpers.advancement;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

import xyz.wagyourtail.jsmacros.client.access.IAdvancementManager;
import xyz.wagyourtail.jsmacros.client.access.IClientAdvancementManager;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class AdvancementManagerHelper extends BaseHelper<AdvancementManager> {

    public AdvancementManagerHelper(AdvancementManager advancementManager) {
        super(advancementManager);
    }

    /**
     * @return
     */
    public Map<String, AdvancementHelper> getAdvancementsWithIdentifier() {
        return ((IAdvancementManager) base).jsmacros_getAdvancementMap().entrySet().stream().collect(Collectors.toMap(
                identifierAdvancementEntry -> identifierAdvancementEntry.getKey().toString(),
                identifierAdvancementEntry -> new AdvancementHelper(identifierAdvancementEntry.getValue())
        ));
    }

    /**
     * @return
     */
    public List<AdvancementHelper> getAdvancements() {
        return base.getAdvancements().stream().map(AdvancementHelper::new).toList();
    }

    /**
     * @return
     */
    public List<AdvancementHelper> getStartedAdvancements() {
        return getProgressStream().filter(advancementProgressEntry -> !advancementProgressEntry.getValue().isAnyObtained()).map(advancementProgressEntry -> new AdvancementHelper(advancementProgressEntry.getKey())).toList();
    }

    /**
     * @return
     */
    public List<AdvancementHelper> getMissingAdvancements() {
        return getProgressStream().filter(advancementProgressEntry -> !advancementProgressEntry.getValue().isDone()).map(advancementProgressEntry -> new AdvancementHelper(advancementProgressEntry.getKey())).toList();
    }

    /**
     * @return
     */
    public List<AdvancementHelper> getCompletedAdvancements() {
        return getProgressStream().filter(advancementProgressEntry -> advancementProgressEntry.getValue().isDone()).map(advancementProgressEntry -> new AdvancementHelper(advancementProgressEntry.getKey())).toList();
    }

    /**
     * @return
     */
    public List<AdvancementHelper> getRootAdvancements() {
        return StreamSupport.stream(base.getRoots().spliterator(), false).map(AdvancementHelper::new).toList();
    }

    /**
     * @return
     */
    public List<AdvancementHelper> getDependents() {
        return ((IAdvancementManager) base).jsmacros_getDependents().stream().map(AdvancementHelper::new).toList();
    }

    /**
     * @return
     */
    public AdvancementHelper getAdvancement(String identifier) {
        return new AdvancementHelper(base.get(new Identifier(identifier)));
    }

    /**
     * @return
     */
    public Map<AdvancementHelper, AdvancementProgressHelper> getAdvancementsProgress() {
        return getProgressStream().collect(Collectors.toMap(
                advancementProgressEntry -> new AdvancementHelper(advancementProgressEntry.getKey()),
                advancementProgressEntry -> new AdvancementProgressHelper(advancementProgressEntry.getValue())
        ));
    }

    /**
     * @return
     */
    public AdvancementHelper getAdvancementProgress(String identifier) {
        return new AdvancementHelper(base.get(new Identifier(identifier)));
    }

    private Stream<Map.Entry<Advancement, AdvancementProgress>> getProgressStream() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        return ((IClientAdvancementManager) player.networkHandler.getAdvancementHandler()).jsmacros_getAdvancementProgress().entrySet().stream();
    }

}
