package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.mob.VindicatorEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class VindicatorEntityHelper extends IllagerEntityHelper<VindicatorEntity> {

    public VindicatorEntityHelper(VindicatorEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this vindicator is johnny, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isJohnny() {
        return base.hasCustomName() && base.getCustomName().getString().equals("Johnny");
    }

}
