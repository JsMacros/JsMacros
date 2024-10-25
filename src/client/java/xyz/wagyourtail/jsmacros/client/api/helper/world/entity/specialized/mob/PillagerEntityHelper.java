package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.item.Items;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class PillagerEntityHelper extends IllagerEntityHelper<PillagerEntity> {

    public PillagerEntityHelper(PillagerEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this pillager is a captain, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isCaptain() {
        return base.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.WHITE_BANNER);
    }

}
