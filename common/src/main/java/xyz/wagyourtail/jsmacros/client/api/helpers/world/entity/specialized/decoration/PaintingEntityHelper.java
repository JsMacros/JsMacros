package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.decoration;

import net.minecraft.entity.decoration.painting.PaintingEntity;

import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class PaintingEntityHelper extends EntityHelper<PaintingEntity> {

    public PaintingEntityHelper(PaintingEntity base) {
        super(base);
    }

    /**
     * @return the width of this painting.
     *
     * @since 1.8.4
     */
    public int getWidth() {
        return base.motive.getWidth();
    }

    /**
     * @return the height of this painting.
     *
     * @since 1.8.4
     */
    public int getHeight() {
        return base.motive.getWidth();
    }

    /**
     * @return the identifier of this painting's art.
     *
     * @since 1.8.4
     */
    public String getIdentifier() {
        return Registry.PAINTING_MOTIVE.getId(base.motive).toString();
    }

}
