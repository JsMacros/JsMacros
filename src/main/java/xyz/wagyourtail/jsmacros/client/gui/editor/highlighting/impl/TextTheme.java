package xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl;

import net.minecraft.text.TextColor;
import xyz.wagyourtail.jsmacros.client.JsMacros;

import javax.annotation.Nullable;
import java.util.Map;

public class TextTheme {
    Map<String, short[]> themeData = JsMacros.core.config.options.getThemeData();
    
    @Nullable
    public TextColor getColorForToken(String name) {
        if (!themeData.containsKey(name)) return null;
        short[] color = themeData.get(name);
        return TextColor.fromRgb((color[0] & 255) << 16 | (color[1] & 255) << 8 | (color[2] & 255));
    }
}
