package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface IClientAdvancementManager {

    Map<Advancement, AdvancementProgress> jsmacros_getAdvancementProgress();

}
