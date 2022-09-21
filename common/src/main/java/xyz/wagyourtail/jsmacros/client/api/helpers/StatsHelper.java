package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import xyz.wagyourtail.jsmacros.client.mixins.access.MixinStatHandler;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class StatsHelper extends BaseHelper<StatHandler> {
    public StatsHelper(StatHandler base) {
        super(base);
    }

    public List<String> getStatList() {
        return ((MixinStatHandler) base).getStatMap().keySet().stream().map(Stat::getType).map(StatType::getTranslationKey).collect(Collectors.toList());
    }

    public Text getStatText(String statKey) {
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            if (stat.getType().getTranslationKey().equals(statKey)) {
                return stat.getType().getName();
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public int getRawStatValue(String statKey) {
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            if (stat.getType().getTranslationKey().equals(statKey)) {
                return base.getStat(stat);
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public String getFormattedStatValue(String statKey) {
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            if (stat.getType().getTranslationKey().equals(statKey)) {
                return stat.format(base.getStat(stat));
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public Map<String, String> getFormattedStatMap() {
        Map<String, String> map = new HashMap<>();
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            map.put(stat.getType().getTranslationKey(), stat.format(base.getStat(stat)));
        }
        return map;
    }

    public Map<String, Integer> getRawStatMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            map.put(stat.getType().getTranslationKey(), base.getStat(stat));
        }
        return map;
    }

    /**
     * @param id the identifier of the entity.
     * @return how many times the player has killed the entity.
     *
     * @since 1.8.4
     */
    public int getEntityKilled(String id) {
        return getStat(Stats.KILLED, Registry.ENTITY_TYPE, id);
    }

    /**
     * @param id the identifier of the entity.
     * @return how many times the player has killed the specified entity.
     *
     * @since 1.8.4
     */
    public int getKilledByEntity(String id) {
        return getStat(Stats.KILLED_BY, Registry.ENTITY_TYPE, id);
    }

    /**
     * @param id the identifier of the block
     * @return how many times the player has mined the block.
     *
     * @since 1.8.4
     */
    public int getBlockMined(String id) {
        return getStat(Stats.MINED, Registry.BLOCK, id);
    }

    /**
     * @param id the identifier of the item.
     * @return how many times the player has broken the item.
     *
     * @since 1.8.4
     */
    public int getItemBroken(String id) {
        return getStat(Stats.BROKEN, Registry.ITEM, id);
    }

    /**
     * @param id the identifier of the item.
     * @return how many times the player has crafted the item.
     *
     * @since 1.8.4
     */
    public int getItemCrafted(String id) {
        return getStat(Stats.CRAFTED, Registry.ITEM, id);
    }

    /**
     * @param id the identifier of the item.
     * @return how many times the player has used the item.
     *
     * @since 1.8.4
     */
    public int getItemUsed(String id) {
        return getStat(Stats.USED, Registry.ITEM, id);
    }

    /**
     * @param id the identifier of the item.
     * @return how many times the player has picked up the item.
     *
     * @since 1.8.4
     */
    public int getItemPickedUp(String id) {
        return getStat(Stats.PICKED_UP, Registry.ITEM, id);
    }

    /**
     * @param id the identifier of the item.
     * @return how many times the player has dropped the item.
     *
     * @since 1.8.4
     */
    public int getItemDropped(String id) {
        return getStat(Stats.DROPPED, Registry.ITEM, id);
    }

    /**
     * @param id the identifier of the custom stat.
     * @return the value of the custom stat.
     *
     * @since 1.8.4
     */
    public int getCustomStat(String id) {
        return base.getStat(Stats.CUSTOM.getOrCreateStat(new Identifier(id)));
    }

    private <T> int getStat(StatType<T> type, Registry<T> registry, String id) {
        return base.getStat(type.getOrCreateStat(registry.get(Identifier.tryParse(id))));
    }

    /**
     * @param id the identifier of the custom stat.
     * @return the formatted value of the custom stat.
     *
     * @since 1.8.4
     */
    public String getCustomFormattedStat(String id) {
        Stat<Identifier> stat = Stats.CUSTOM.getOrCreateStat(new Identifier(id));
        return stat.format(base.getStat(stat));
    }

    /**
     * Used to request an update of the statistics from the server.
     *
     * @since 1.8.4
     */
    public void updateStatistics() {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.getNetworkHandler() != null;
        mc.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
    }
    
}