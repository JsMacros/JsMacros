package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity;

import net.minecraft.entity.passive.VillagerEntity;

/**
 * @since 1.6.3
 */
@SuppressWarnings("unused")
public class VillagerEntityHelper extends MerchantEntityHelper<VillagerEntity> {

    public VillagerEntityHelper(VillagerEntity e) {
        super(e);
    }

    /**
     * @since 1.6.3
     * @return #VillagerProfession#
     */
    public String getProfession() {
        return base.getVillagerData().getProfession().id();
    }

    /**
     * @since 1.6.3
     * @return #VillagerStyle#
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

}
