package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.passive.VillagerEntity;

/**
 * @since 1.6.3
 */
public class VillagerEntityHelper extends MerchantEntityHelper<VillagerEntity> {

    public VillagerEntityHelper(VillagerEntity e) {
        super(e);
    }

    /**
     * @since 1.6.3
     * @return
     */
    public String getProfession() {
        return base.getVillagerData().getProfession().toString();
    }

    /**
     * @since 1.6.3
     * @return
     */
    public String getStyle() {
        return base.getVillagerData().getType().toString();
    }

    /**
     * @since 1.6.3
     * @return
     */
    public int getLevel() {
        return base.getVillagerData().getLevel();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("^Merchant", "Villager");
    }

}
