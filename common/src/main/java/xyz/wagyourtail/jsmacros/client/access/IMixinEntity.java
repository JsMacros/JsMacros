package xyz.wagyourtail.jsmacros.client.access;

public interface IMixinEntity {

    public void jsmacros_setGlowingColor(int glowingColor);

    public int jsmacros_getGlowingColor();

    public void jsmacros_resetColor();

    /**
     * @param glowing 1 for enabled, 2 for forced
     */
    public void jsmacros_setForceGlowing(int glowing);
}