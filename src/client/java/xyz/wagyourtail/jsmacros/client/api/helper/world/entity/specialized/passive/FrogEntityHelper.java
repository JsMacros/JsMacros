package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FrogEntityHelper extends AnimalEntityHelper<FrogEntity> {
    MinecraftClient mc = MinecraftClient.getInstance();

    public FrogEntityHelper(FrogEntity base) {
        super(base);
    }

    /**
     * @return the variant of this frog.
     * @since 1.8.4
     */
    @DocletReplaceReturn("FrogVariant")
    public String getVariant() {
        return mc.getNetworkHandler().getRegistryManager().getOrThrow(RegistryKeys.FROG_VARIANT).getId(base.getVariant().value()).toString();
    }

    /**
     * @return the target of this frog, or {@code null} if it has none.
     * @since 1.8.4
     */
    @Nullable
    public EntityHelper<?> getTarget() {
        return base.getFrogTarget().map(EntityHelper::create).orElse(null);
    }

    /**
     * @return {@code true} if this frog is croaking, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isCroaking() {
        return base.croakingAnimationState.isRunning();
    }

}
