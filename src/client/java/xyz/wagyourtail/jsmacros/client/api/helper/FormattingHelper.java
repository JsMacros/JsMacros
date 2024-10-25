package xyz.wagyourtail.jsmacros.client.api.helper;

import net.minecraft.util.Formatting;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class FormattingHelper extends BaseHelper<Formatting> {

    public FormattingHelper(Formatting base) {
        super(base);
    }

    /**
     * @return the color value of this formatting.
     * @since 1.8.4
     */
    public int getColorValue() {
        return base.getColorValue();
    }

    /**
     * @return the index of this formatting or {@code -1} if this formatting is a modifier.
     * @since 1.8.4
     */
    public int getColorIndex() {
        return base.getColorIndex();
    }

    /**
     * @return the name of this formatting.
     * @since 1.8.4
     */
    public String getName() {
        return base.getName();
    }

    /**
     * The color code can be used with the paragraph to color text.
     *
     * @return the color code of this formatting.
     * @since 1.8.4
     */
    public char getCode() {
        return base.getCode();
    }

    /**
     * @return {@code true} if this formatting is a color, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isColor() {
        return base.isColor();
    }

    /**
     * @return {@code true} if this formatting is a modifier, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isModifier() {
        return base.isModifier();
    }

    @Override
    public String toString() {
        return String.format("FormattingHelper:{\"index\": %d, \"color\": %d, \"name\": \"%s\", \"code\": \"%s\", \"isColor\": %b, \"isModifier\": %b}", getColorIndex(), getColorValue(), getName(), getCode(), isColor(), isModifier());
    }

}
