package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.passive;

import net.minecraft.entity.passive.IronGolemEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.MobEntityHelper;

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
     *
     * @since 1.8.4
     */
    public boolean isPlayerCreated() {
        return base.isPlayerCreated();
    }

    /**
     * @return get the type of cracks this iron golem has.
     *
     * @since 1.8.4
     */
    public String getCrack() {
        return switch (base.getCrack()) {
            case NONE -> "none";
            case LOW -> "low";
            case MEDIUM -> "medium";
            case HIGH -> "high";
        };
    }

}
