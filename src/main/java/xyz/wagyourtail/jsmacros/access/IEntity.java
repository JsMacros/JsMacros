package xyz.wagyourtail.jsmacros.access;

public interface IEntity {

    void jsmacros_setGlowingColor(int glowingColor);

    void jsmacros_resetColor();

    /**
     * @param glowing 1 for enabled, 2 for forced
     */
    void jsmacros_setForceGlowing(int glowing);

}
