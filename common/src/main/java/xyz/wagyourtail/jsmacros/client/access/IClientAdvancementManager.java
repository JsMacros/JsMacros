package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public interface IClientAdvancementManager {

    Map<Advancement, AdvancementProgress> jsmacros_getAdvancementProgress();

}
