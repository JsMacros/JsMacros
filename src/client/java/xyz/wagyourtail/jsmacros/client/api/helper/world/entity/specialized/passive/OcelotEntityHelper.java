package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.OcelotEntity;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinOcelotEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class OcelotEntityHelper extends AnimalEntityHelper<OcelotEntity> {

    public OcelotEntityHelper(OcelotEntity base) {
        super(base);
    }

    /**
     * Ocelots trust players after being fed with cod or salmon.
     *
     * @return {@code true} if this ocelot is trusting player and not running away form them,
     * {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isTrusting() {
        return ((MixinOcelotEntity) base).invokeIsTrusting();
    }

}
