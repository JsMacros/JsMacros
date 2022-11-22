package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.potion.Potion;

public interface IBeaconScreen {

    Potion jsmacros_getPrimaryEffect();

    void jsmacros_setPrimaryEffect(Potion effect);

    Potion jsmacros_getSecondaryEffect();

    void jsmacros_setSecondaryEffect(Potion effect);

    int jsmacros_getLevel();

    boolean jsmacros_sendBeaconPacket();
}
