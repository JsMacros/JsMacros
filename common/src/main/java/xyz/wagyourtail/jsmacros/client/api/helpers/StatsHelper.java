package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
                return new TranslatableText(stat.getType().getTranslationKey());
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
}
