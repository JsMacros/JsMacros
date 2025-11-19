package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.StriderEntity;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class StriderEntityHelper extends AnimalEntityHelper<StriderEntity> {

    public StriderEntityHelper(StriderEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this strider is saddled, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSaddled() {
        return base.hasSaddleEquipped();
    }

    /**
     * @return {@code true} if this strider is shivering in the cold, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isShivering() {
        return base.isCold();
    }

}
