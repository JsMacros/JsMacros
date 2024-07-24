package xyz.wagyourtail.jsmacros.client.access;

public interface IMixinEntity {

    void jsmacros_setGlowingColor(int glowingColor);

    void jsmacros_resetColor();

    /**
     * @param glowing 1 for enabled, 2 for forced
     */
    void jsmacros_setForceGlowing(int glowing);

}
