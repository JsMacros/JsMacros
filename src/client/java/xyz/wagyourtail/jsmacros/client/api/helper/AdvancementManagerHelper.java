package xyz.wagyourtail.jsmacros.client.api.helper;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinAdvancementManager;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinClientAdvancementManager;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AdvancementManagerHelper extends BaseHelper<AdvancementManager> {

    public AdvancementManagerHelper(AdvancementManager advancementManager) {
        super(advancementManager);
    }

    /**
     * @return a map of all advancement ids and their advancement.
     * @since 1.8.4
     */
    @DocletReplaceReturn("JavaMap<AdvancementId, AdvancementHelper>")
    public Map<String, AdvancementHelper> getAdvancementsForIdentifiers() {
        return ((MixinAdvancementManager) base).getAdvancements().entrySet().stream().collect(Collectors.toMap(
                identifierAdvancementEntry -> identifierAdvancementEntry.getKey().toString(),
                identifierAdvancementEntry -> new AdvancementHelper(identifierAdvancementEntry.getValue())
        ));
    }

    /**
     * @return a list of all advancements.
     * @since 1.8.4
     */
    public List<AdvancementHelper> getAdvancements() {
        return base.getAdvancements().stream().map(AdvancementHelper::new).collect(Collectors.toList());
    }

    /**
     * Started advancements are advancements that have been started, so at least one task has been
     * completed so far, but not fully completed.
     *
     * @return a list of all started advancements.
     * @since 1.8.4
     */
    public List<AdvancementHelper> getStartedAdvancements() {
        return getProgressStream().filter(progress -> !progress.getValue().isDone() && progress.getValue().isAnyObtained()).map(advancementProgressEntry -> new AdvancementHelper(base.get(advancementProgressEntry.getKey()))).collect(Collectors.toList());
    }

    /**
     * @return a list of all missing advancements.
     * @since 1.8.4
     */
    public List<AdvancementHelper> getMissingAdvancements() {
        return getProgressStream().filter(advancementProgressEntry -> !advancementProgressEntry.getValue().isDone()).map(advancementProgressEntry -> new AdvancementHelper(base.get(advancementProgressEntry.getKey()))).collect(Collectors.toList());
    }

    /**
     * @return a list of all completed advancements.
     * @since 1.8.4
     */
    public List<AdvancementHelper> getCompletedAdvancements() {
        return getProgressStream().filter(advancementProgressEntry -> advancementProgressEntry.getValue().isDone()).map(advancementProgressEntry -> new AdvancementHelper(base.get(advancementProgressEntry.getKey()))).collect(Collectors.toList());
    }

    /**
     * @return a list of all the root advancements.
     * @since 1.8.4
     */
    public List<AdvancementHelper> getRootAdvancements() {
        return StreamSupport.stream(base.getRoots().spliterator(), false).map(AdvancementHelper::new).collect(Collectors.toList());
    }

    /**
     * @return a list of all advancements that are not a root.
     * @since 1.8.4
     */
    public List<AdvancementHelper> getSubAdvancements() {
        return ((MixinAdvancementManager) base).getDependents().stream().map(AdvancementHelper::new).collect(Collectors.toList());
    }

    /**
     * @param identifier the identifier of the advancement
     * @return the advancement for the given identifier.
     * @since 1.8.4
     */
    @DocletReplaceParams("identifier: CanOmitNamespace<AdvancementId>")
    public AdvancementHelper getAdvancement(String identifier) {
        return new AdvancementHelper(base.get(RegistryHelper.parseIdentifier(identifier)));
    }

    /**
     * @return a map of all advancements and their progress.
     * @since 1.8.4
     */
    public Map<AdvancementHelper, AdvancementProgressHelper> getAdvancementsProgress() {
        return getProgressStream().collect(Collectors.toMap(
                advancementProgressEntry -> new AdvancementHelper(base.get(advancementProgressEntry.getKey())),
                advancementProgressEntry -> new AdvancementProgressHelper(advancementProgressEntry.getValue())
        ));
    }

    /**
     * @return the progress of the given advancement.
     * @since 1.8.4
     */
    @DocletReplaceParams("identifier: CanOmitNamespace<AdvancementId>")
    public AdvancementProgressHelper getAdvancementProgress(String identifier) {
        assert MinecraftClient.getInstance().player != null;
        return new AdvancementProgressHelper(((MixinClientAdvancementManager) MinecraftClient.getInstance().player.networkHandler.getAdvancementHandler()).getAdvancementProgresses().get(base.get(RegistryHelper.parseIdentifier(identifier)).getAdvancementEntry()));
    }

    private Stream<Map.Entry<AdvancementEntry, AdvancementProgress>> getProgressStream() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null;
        return ((MixinClientAdvancementManager) player.networkHandler.getAdvancementHandler()).getAdvancementProgresses().entrySet().stream();
    }

    @Override
    public String toString() {
        return String.format("AdvancementManagerHelper:{\"started\": %d, \"missing\": %d, \"completed\": %d}", getStartedAdvancements().size(), getMissingAdvancements().size(), getCompletedAdvancements().size());
    }

}
