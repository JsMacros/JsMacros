package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.mob;

import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.item.Items;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class DrownedEntityHelper extends ZombieEntityHelper<DrownedEntity> {

    public DrownedEntityHelper(DrownedEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this drowned is holding a trident, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasTrident() {
        return base.getMainHandStack().isOf(Items.TRIDENT);
    }

    /**
     * @return {@code true} if this drowned is holding a nautilus shell, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasNautilusShell() {
        return base.getMainHandStack().isOf(Items.NAUTILUS_SHELL);
    }

}
