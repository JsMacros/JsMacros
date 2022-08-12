package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.mob;

import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.registry.Registry;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class ZombieVillagerEntityHelper extends ZombieEntityHelper<ZombieVillagerEntity> {

    public ZombieVillagerEntityHelper(ZombieVillagerEntity base) {
        super(base);
    }

    /**
     * @return {@code true} if this zombie villager is currently being converted back to a villager,
     *         {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isConvertingToVillager() {
        return base.isConverting();
    }

    /**
     * @return the type of biome the villager belonged to it was converted to a zombie.
     *
     * @since 1.8.4
     */
    public String getVillagerBiomeType() {
        return Registry.VILLAGER_TYPE.getId(base.getVillagerData().getType()).toString();
    }

    /**
     * @return the profession of the villager before it was converted to a zombie.
     *
     * @since 1.8.4
     */
    public String getProfession() {
        return base.getVillagerData().getProfession().getId();
    }

    /**
     * @return the level of the villager before it was converted to a zombie.
     *
     * @since 1.8.4
     */
    public int getLevel() {
        return base.getVillagerData().getLevel();
    }

}
