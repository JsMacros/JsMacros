package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public interface IBeaconScreen {

    RegistryEntry<StatusEffect> jsmacros_getPrimaryEffect();

    void jsmacros_setPrimaryEffect(RegistryEntry<StatusEffect> effect);

    RegistryEntry<StatusEffect> jsmacros_getSecondaryEffect();

    void jsmacros_setSecondaryEffect(RegistryEntry<StatusEffect> effect);

}
