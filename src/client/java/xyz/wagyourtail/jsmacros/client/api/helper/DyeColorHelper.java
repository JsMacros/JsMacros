package xyz.wagyourtail.jsmacros.client.api.helper;

import net.minecraft.util.DyeColor;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
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
     * @since 1.8.4
     */
    @DocletReplaceReturn("DyeColorName")
    public String getName() {
        return base.getId();
    }

    /**
     * @return the color's identifier.
     * @since 1.8.4
     */
    public int getId() {
        return base.getIndex();
    }

    /**
     * @return the color's rgb value.
     * @since 1.8.4
     */
    public int getColorValue() {
        return base.getEntityColor();
    }

    /**
     * @return the color's variation when used in fireworks.
     * @since 1.8.4
     */
    public int getFireworkColor() {
        return base.getFireworkColor();
    }

    /**
     * @return the color's variation when used on signs.
     * @since 1.8.4
     */
    public int getSignColor() {
        return base.getSignColor();
    }

}
