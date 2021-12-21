package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.entity.effect.StatusEffect;

public interface IBeaconScreen {

    StatusEffect jsmacros_getPrimaryEffect();

    void jsmacros_setPrimaryEffect(StatusEffect effect);

    StatusEffect jsmacros_getSecondaryEffect();

    void jsmacros_setSecondaryEffect(StatusEffect effect);
}
