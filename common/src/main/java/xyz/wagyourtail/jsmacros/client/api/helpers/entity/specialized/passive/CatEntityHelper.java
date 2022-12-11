package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive;

import net.minecraft.entity.passive.CatEntity;
import net.minecraft.registry.Registries;

import xyz.wagyourtail.jsmacros.client.api.helpers.DyeColorHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CatEntityHelper extends TameableEntityHelper<CatEntity> {

    public CatEntityHelper(CatEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this cat is sleeping, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSleeping() {
        return base.isSleeping();
    }

    /**
     * @return the color of this cat's collar.
     *
     * @since 1.8.4
     */
    public DyeColorHelper getCollarColor() {
        return new DyeColorHelper(base.getCollarColor());
    }

    /**
     * @return the variant of this cat.
     *
     * @since 1.8.4
     */
    public String getVariant() {
        return Registries.CAT_VARIANT.getId(base.getVariant()).toString();
    }

}
