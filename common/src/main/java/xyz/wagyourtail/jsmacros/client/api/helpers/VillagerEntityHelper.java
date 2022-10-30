package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.passive.VillagerEntity;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinVillager;

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
    public int getProfession() {
        return base.profession();
    }

    /**
     * @since 1.6.3
     * @return
     */
    public String getStyle() {
        return "";
    }

    /**
     * @since 1.6.3
     * @return
     */
    public int getLevel() {
        return ((MixinVillager) base).getCareerLevel();
    }

    @Override
    public String toString() {
        return super.toString().replaceFirst("^Merchant", "Villager");
    }

}
