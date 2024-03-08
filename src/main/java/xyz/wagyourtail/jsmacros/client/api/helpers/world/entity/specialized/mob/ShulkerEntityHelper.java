package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.mob;

import net.minecraft.entity.mob.ShulkerEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helpers.DyeColorHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.DirectionHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.MobEntityHelper;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinShulkerEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ShulkerEntityHelper extends MobEntityHelper<ShulkerEntity> {

    public ShulkerEntityHelper(ShulkerEntity base) {
        super(base);
    }

    public boolean isClosed() {
        return ((MixinShulkerEntity) base).invokeIsClosed();
    }

    public DirectionHelper getAttachedSide() {
        return new DirectionHelper(base.getAttachedFace());
    }

    @Nullable
    public DyeColorHelper getColor() {
        return base.getColor() == null ? null : new DyeColorHelper(base.getColor());
    }

}
