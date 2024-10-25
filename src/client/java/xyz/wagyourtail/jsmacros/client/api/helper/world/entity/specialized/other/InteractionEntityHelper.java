package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.other;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.access.IMixinInteractionEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinInteractionEntity2;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class InteractionEntityHelper extends EntityHelper<InteractionEntity> {

    public InteractionEntityHelper(InteractionEntity base) {
        super(base);
    }

    /**
     * @since 1.9.1
     */
    public void setCanHit(boolean value) {
        ((IMixinInteractionEntity) base).jsmacros_setCanHitOverride(value);
    }

    /**
     * @since 1.9.1
     */
    @Nullable
    public EntityHelper<?> getLastAttacker() {
        LivingEntity e = base.getLastAttacker();
        return e == null ? null : EntityHelper.create(e);
    }

    /**
     * @since 1.9.1
     */
    @Nullable
    public EntityHelper<?> getLastInteracted() {
        LivingEntity e = base.getTarget();
        return e == null ? null : EntityHelper.create(e);
    }

    /**
     * @since 1.9.1
     */
    public float getWidth() {
        return ((MixinInteractionEntity2) base).callGetInteractionWidth();
    }

    /**
     * @since 1.9.1
     */
    public float getHeight() {
        return ((MixinInteractionEntity2) base).callGetInteractionHeight();
    }

    /**
     * @since 1.9.1
     */
    public boolean shouldRespond() {
        return ((MixinInteractionEntity2) base).callShouldRespond();
    }

}
