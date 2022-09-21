package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.advancement.Advancement;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface IAdvancementManager {

    Map<Identifier, Advancement> jsmacros_getAdvancementMap();

    Set<Advancement> jsmacros_getDependents();

}
