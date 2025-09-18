package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.RabbitEntity;
import xyz.wagyourtail.doclet.DocletReplaceReturn;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class RabbitEntityHelper extends AnimalEntityHelper<RabbitEntity> {

    public RabbitEntityHelper(RabbitEntity base) {
        super(base);
    }

    /**
     * @return the variant of this rabbit.
     * @since 1.8.4
     */
    @DocletReplaceReturn("RabbitVariant")
    public String getVariant() {
        return base.getVariant().asString();
    }

    /**
     * @return {@code true} if this rabbit is a killer bunny, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isKillerBunny() {
        return base.getVariant() == RabbitEntity.Variant.EVIL;
    }

}
