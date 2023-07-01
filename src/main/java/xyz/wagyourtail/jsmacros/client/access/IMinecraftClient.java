package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.font.FontManager;

public interface IMinecraftClient {
    FontManager jsmacros_getFontManager();

    void jsmacros_doItemUse();

    void jsmacros_doAttack();

}
