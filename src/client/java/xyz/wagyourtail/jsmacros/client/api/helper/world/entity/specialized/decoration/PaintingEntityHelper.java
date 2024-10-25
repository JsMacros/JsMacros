package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.decoration;

import net.minecraft.entity.decoration.painting.PaintingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;

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
     * @since 1.8.4
     */
    public int getWidth() {
        return base.getVariant().value().width();
    }

    /**
     * @return the height of this painting.
     * @since 1.8.4
     */
    public int getHeight() {
        return base.getVariant().value().height();
    }

    /**
     * @return the identifier of this painting's art.
     * @since 1.8.4
     */
    @Nullable
    @DocletReplaceReturn("PaintingId")
    public String getIdentifier() {
        return base.getVariant().getKey().map(paintingVariantRegistryKey -> paintingVariantRegistryKey.getValue().toString()).orElse(null);
    }

}
