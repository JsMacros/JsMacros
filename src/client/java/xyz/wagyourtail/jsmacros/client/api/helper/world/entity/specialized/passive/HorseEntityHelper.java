package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.HorseEntity;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinHorseEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class HorseEntityHelper extends AbstractHorseEntityHelper<HorseEntity> {

    public HorseEntityHelper(HorseEntity base) {
        super(base);
    }

    /**
     * @return the variant of this horse.
     * @since 1.8.4
     */
    public int getVariant() {
        return ((MixinHorseEntity) base).invokeGetHorseVariant();
    }

}
