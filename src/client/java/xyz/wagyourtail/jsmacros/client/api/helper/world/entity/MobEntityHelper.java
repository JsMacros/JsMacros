package xyz.wagyourtail.jsmacros.client.api.helper.world.entity;

import net.minecraft.entity.mob.MobEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class MobEntityHelper<T extends MobEntity> extends LivingEntityHelper<T> {

    public MobEntityHelper(T base) {
        super(base);
    }

    /**
     * @return {@code true} if the entity is currently attacking something, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isAttacking() {
        return base.isAttacking();
    }

    /**
     * Mobs which have there AI disabled don't move, attack, or interact with the world by
     * themselves.
     *
     * @return {@code true} if the entity's AI is disabled, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAiDisabled() {
        return base.isAiDisabled();
    }

}
