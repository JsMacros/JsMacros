package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.util.DyeColor;

import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class DyeColorHelper extends BaseHelper<DyeColor> {

    public DyeColorHelper(DyeColor base) {
        super(base);
    }

    /**
     * @return the name of the color.
     *
     * @since 1.8.4
     */
    public String getName() {
        return base.getName();
    }

    /**
     * @return the color's identifier.
     *
     * @since 1.8.4
     */
    public int getId() {
        return base.getId();
    }

    /**
     * @return the color's rgb value.
     *
     * @since 1.8.4
     */
    public int getColorValue() {
        float[] color = base.getColorComponents();
        return (int) (color[0] * 255) << 16 | (int) (color[1] * 255) << 8 | (int) (color[2] * 255);
    }

    /**
     * @return the color's variation when used in fireworks.
     *
     * @since 1.8.4
     */
    public int getFireworkColor() {
        return base.getFireworkColor();
    }

    /**
     * @return the color's variation when used on signs.
     *
     * @since 1.8.4
     */
    public int getSignColor() {
        return base.getSignColor();
    }

}
