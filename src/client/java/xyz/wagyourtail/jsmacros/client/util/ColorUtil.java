package xyz.wagyourtail.jsmacros.client.util;

public class ColorUtil {
    /**
     * Ensures a color doesn't have 0 opacity if it has rgb.
     *
     * @param color input color with potentially no opacity
     * @return color with added opacity, if needed
     */
    public static int fixAlpha(int color) {
        int alpha = color & 0xFF000000;
        int rgb = color & 0xFFFFFF;
        return alpha == 0 && rgb > 0 ? color | 0xFF000000 : color;
    }
}
