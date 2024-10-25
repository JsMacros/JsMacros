package xyz.wagyourtail.jsmacros.client.api.helper;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinStatHandler;
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
        return ((MixinStatHandler) base).getStatMap().keySet().stream().map(this::getTranslationKey).collect(Collectors.toList());
    }

    public Text getStatText(String statKey) {
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            if (getTranslationKey(stat).equals(statKey)) {
                return stat.getType().getName();
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public int getRawStatValue(String statKey) {
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            if (getTranslationKey(stat).equals(statKey)) {
                return base.getStat(stat);
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public String getFormattedStatValue(String statKey) {
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            if (getTranslationKey(stat).equals(statKey)) {
                return stat.format(base.getStat(stat));
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    private String getTranslationKey(Stat<?> stat) {
        if (stat.getType().getName() instanceof TranslatableTextContent t) {
            return t.getKey();
        } else {
            return stat.getType().getName().getString();
        }
    }

    public Map<String, String> getFormattedStatMap() {
        Map<String, String> map = new HashMap<>();
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            map.put(getTranslationKey(stat), stat.format(base.getStat(stat)));
        }
        return map;
    }

    public Map<String, Integer> getRawStatMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Stat<?> stat : ImmutableSet.copyOf(((MixinStatHandler) base).getStatMap().keySet())) {
            map.put(getTranslationKey(stat), base.getStat(stat));
        }
        return map;
    }

    /**
     * @param id the identifier of the entity
     * @return how many times the player has killed the entity.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: EntityId")
    public int getEntityKilled(String id) {
        return getStat(Stats.KILLED, Registries.ENTITY_TYPE, id);
    }

    /**
     * @param id the identifier of the entity
     * @return how many times the player has killed the specified entity.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: EntityId")
    public int getKilledByEntity(String id) {
        return getStat(Stats.KILLED_BY, Registries.ENTITY_TYPE, id);
    }

    /**
     * @param id the identifier of the block
     * @return how many times the player has mined the block.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: BlockId")
    public int getBlockMined(String id) {
        return getStat(Stats.MINED, Registries.BLOCK, id);
    }

    /**
     * @param id the identifier of the item
     * @return how many times the player has broken the item.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: ItemId")
    public int getItemBroken(String id) {
        return getStat(Stats.BROKEN, Registries.ITEM, id);
    }

    /**
     * @param id the identifier of the item
     * @return how many times the player has crafted the item.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: ItemId")
    public int getItemCrafted(String id) {
        return getStat(Stats.CRAFTED, Registries.ITEM, id);
    }

    /**
     * @param id the identifier of the item
     * @return how many times the player has used the item.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: ItemId")
    public int getItemUsed(String id) {
        return getStat(Stats.USED, Registries.ITEM, id);
    }

    /**
     * @param id the identifier of the item
     * @return how many times the player has picked up the item.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: ItemId")
    public int getItemPickedUp(String id) {
        return getStat(Stats.PICKED_UP, Registries.ITEM, id);
    }

    /**
     * @param id the identifier of the item
     * @return how many times the player has dropped the item.
     * @since 1.8.4
     */
    @DocletReplaceParams("id: ItemId")
    public int getItemDropped(String id) {
        return getStat(Stats.DROPPED, Registries.ITEM, id);
    }

    /**
     * @param id the identifier of the custom stat
     * @return the value of the custom stat.
     * @since 1.8.4
     */
    public int getCustomStat(String id) {
        return base.getStat(Stats.CUSTOM.getOrCreateStat(RegistryHelper.parseIdentifier(id)));
    }

    private <T> int getStat(StatType<T> type, Registry<T> registry, String id) {
        return base.getStat(type.getOrCreateStat(registry.get(RegistryHelper.parseIdentifier(id))));
    }

    /**
     * @param id the identifier of the custom stat
     * @return the formatted value of the custom stat.
     * @since 1.8.4
     */
    public String getCustomFormattedStat(String id) {
        Stat<Identifier> stat = Stats.CUSTOM.getOrCreateStat(RegistryHelper.parseIdentifier(id));
        return stat.format(base.getStat(stat));
    }

    /**
     * Used to request an update of the statistics from the server.
     *
     * @return self for chaining.
     * @since 1.8.4
     */
    public StatsHelper updateStatistics() {
        MinecraftClient mc = MinecraftClient.getInstance();
        assert mc.getNetworkHandler() != null;
        mc.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
        return this;
    }

    @Override
    public String toString() {
        return String.format("StatsHelper:{%s}", getFormattedStatMap());
    }

}
