package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
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
        return ((MixinStatHandler) base).getField_9047().keySet().stream().map(s -> s.name).collect(Collectors.toList());
    }

    public Text getStatText(String statKey) {
        for (Stat stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_9047().keySet())) {
            if (stat.name.equals(statKey)) {
                return stat.getText();
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public int getRawStatValue(String statKey) {
        for (Stat stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_9047().keySet())) {
            if (stat.equals(statKey)) {
                return base.method_1729(stat);
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public String getFormattedStatValue(String statKey) {
        for (Stat stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_9047().keySet())) {
            if (stat.name.equals(statKey)) {
                return stat.method_2261(base.method_1729(stat));
            }
        }
        throw new IllegalArgumentException("Stat not found: " + statKey);
    }

    public Map<String, String> getFormattedStatMap() {
        Map<String, String> map = new HashMap<>();
        for (Stat stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_9047().keySet())) {
            map.put(stat.name, stat.method_2261(base.method_1729(stat)));
        }
        return map;
    }

    public Map<String, Integer> getRawStatMap() {
        Map<String, Integer> map = new HashMap<>();
        for (Stat stat : ImmutableSet.copyOf(((MixinStatHandler) base).getField_9047().keySet())) {
            map.put(stat.name, base.method_1729(stat));
        }
        return map;
    }
}
