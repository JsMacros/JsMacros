package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.util.ChatStyle;

public interface IStyle {
    ChatStyle jsmacros_setCustomColor(int color);

    boolean hasCustomColor();

    int getCustomColor();
}
