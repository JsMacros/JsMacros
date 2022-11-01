package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.text.Style;

public interface IStyle {
    Style jsmacros_setCustomColor(int color);

    boolean hasCustomColor();

    int getCustomColor();
}
