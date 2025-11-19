package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.registry.RegistryKeys;
import xyz.wagyourtail.jsmacros.client.api.helper.DyeColorHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CatEntityHelper extends TameableEntityHelper<CatEntity> {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public CatEntityHelper(CatEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this cat is sleeping, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSleeping() {
        return base.isSleeping();
    }

    /**
     * @return the color of this cat's collar.
     * @since 1.8.4
     */
    public DyeColorHelper getCollarColor() {
        return new DyeColorHelper(base.getCollarColor());
    }

    /**
     * @return the variant of this cat.
     * @since 1.8.4
     */
    public String getVariant() {
        return mc.getNetworkHandler().getRegistryManager().getOrThrow(RegistryKeys.CAT_VARIANT).getId(base.getVariant().value()).toString();
    }

}
