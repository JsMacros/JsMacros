package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.IronGolemEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class IronGolemEntityHelper extends MobEntityHelper<IronGolemEntity> {

    public IronGolemEntityHelper(IronGolemEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this iron golem was created by a player, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isPlayerCreated() {
        return base.isPlayerCreated();
    }

}
