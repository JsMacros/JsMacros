package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.TameableEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.LivingEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class TameableEntityHelper<T extends TameableEntity> extends AnimalEntityHelper<T> {

    public TameableEntityHelper(T base) {
        super(base);
    }

    /**
     * @return {@code true} if the entity is tamed, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isTamed() {
        return base.isTamed();
    }

    /**
     * @return {@code true} if the entity is sitting, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSitting() {
        return base.isSitting();
    }

    /**
     * @return the owner's uuid, or {@code null} if the entity is not tamed.
     * @since 1.8.4
     */
    @Nullable
    public String getOwner() {
        var owner = base.getOwnerReference();
        return owner != null ? owner.getUuid().toString() : null;
    }

    /**
     * @param owner the possible owner
     * @return {@code true} if the entity is tamed by the given owner, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isOwner(LivingEntityHelper<?> owner) {
        return base.isOwner(owner.getRaw());
    }

}
