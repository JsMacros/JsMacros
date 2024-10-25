package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.projectile;

import net.minecraft.entity.projectile.TridentEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinTridentEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class TridentEntityHelper extends EntityHelper<TridentEntity> {

    public TridentEntityHelper(TridentEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if the trident is enchanted with loyalty, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasLoyalty() {
        return base.getDataTracker().get(((MixinTridentEntity) base).getLoyalty()) > 0;
    }

    /**
     * @return {@code true} if the trident is enchanted, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isEnchanted() {
        return base.isEnchanted();
    }

}
