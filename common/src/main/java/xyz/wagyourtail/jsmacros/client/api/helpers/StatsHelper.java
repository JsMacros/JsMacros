package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinStatHandler;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class StatsHelper extends BaseHelper<StatFileWriter> {
    public StatsHelper(StatFileWriter base) {
        super(base);
    }

    public List<String> getStatList() {
        return ((MixinStatHandler) base).getField_150875_a().keySet().stream().map(s -> s.name).collect(Collectors.toList());
    }

    public IChatComponent getStatText(String statKey) {
        for (StatBase stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_150875_a().keySet())) {
            if (stat.name.equals(statKey)) {
                return stat.getText();
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public int getRawStatValue(String statKey) {
        for (StatBase stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_150875_a().keySet())) {
            if (stat.name.equals(statKey)) {
                return base.func_77444_a(stat);
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public String getFormattedStatValue(String statKey) {
        for (StatBase stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_150875_a().keySet())) {
            if (stat.name.equals(statKey)) {
                return stat.func_75968_a(base.func_77444_a(stat));
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public Map<String, String> getFormattedStatMap() {
        Map<String, String> map = new HashMap<>();
        for (StatBase stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_150875_a().keySet())) {
            map.put(stat.name, stat.func_75968_a(base.func_77444_a(stat)));
        }
        return map;
    }

    public Map<String, Integer> getRawStatMap() {
        Map<String, Integer> map = new HashMap<>();
        for (StatBase stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_150875_a().keySet())) {
            map.put(stat.name, base.func_77444_a(stat));
        }
        return map;
    }
}
