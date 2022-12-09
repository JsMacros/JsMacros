package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.mob;

import net.minecraft.entity.mob.AbstractPiglinEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.MobEntityHelper;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinAbstractPiglinEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class AbstractPiglinEntityHelper<T extends AbstractPiglinEntity> extends MobEntityHelper<T> {

    public AbstractPiglinEntityHelper(T base) {
        super(base);
    }

    /**
     * @return {@code true} if this piglin can be zombified in the current dimension, {@code false}
     *         otherwise.
     *
     * @since 1.8.4
     */
    public boolean canBeZombified() {
        return !((MixinAbstractPiglinEntity) base).invokeIsImmuneToZombification();
    }

}