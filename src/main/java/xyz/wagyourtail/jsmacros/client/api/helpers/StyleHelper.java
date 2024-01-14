package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public class StyleHelper extends BaseHelper<Style> {
    public StyleHelper(Style base) {
        super(base);
    }

    public boolean hasColor() {
        return base.getColor() != null;
    }

    /**
     * @return the color index of this style or {@code -1} if no color is set.
     * @deprecated use {@link #getColorIndex()} instead.
     */
    @Deprecated
    public int getColor() {
        return getColorIndex();
    }

    /**
     * @return the formatting of this style, or {@code null} if no formatting was found.
     * @since 1.8.4
     */
    @Nullable
    public FormattingHelper getFormatting() {
        Formatting f = Formatting.byName(base.getColor().getName());
        return f == null ? null : new FormattingHelper(f);
    }

    /**
     * @return the color index of this style or {@code -1} if no color is set.
     * @since 1.8.4
     */
    public int getColorIndex() {
        if (base.getColor() == null) {
            return -1;
        }
        Formatting f = Formatting.byName(base.getColor().getName());
        return f == null ? -1 : f.getColorIndex();
    }

    /**
     * @return the color value of this style or {@code -1} if it doesn't have one.
     * @since 1.8.4
     */
    public int getColorValue() {
        if (base.getColor() == null) {
            return -1;
        }
        Formatting f = Formatting.byName(base.getColor().getName());
        return f == null || f.getColorValue() == null ? -1 : f.getColorValue();
    }

    /**
     * @return the color name of this style or {@code null} if it has no color.
     * @since 1.8.4
     */
    @Nullable
    public String getColorName() {
        return base.getColor() == null ? null : base.getColor().getName();
    }

    public boolean hasCustomColor() {
        return base.getColor() != null && base.getColor().getName().startsWith("#");
    }

    public int getCustomColor() {
        return base.getColor() == null ? -1 : base.getColor().getRgb();
    }

    public boolean bold() {
        return base.isBold();
    }

    public boolean italic() {
        return base.isItalic();
    }

    public boolean underlined() {
        return base.isUnderlined();
    }

    public boolean strikethrough() {
        return base.isStrikethrough();
    }

    public boolean obfuscated() {
        return base.isObfuscated();
    }

    @DocletReplaceReturn("TextClickAction | 'custom' | null")
    @Nullable
    public String getClickAction() {
        if (base.getClickEvent() == null) {
            return null;
        }
        if (base.getClickEvent() instanceof CustomClickEvent) {
            return "custom";
        }
        return base.getClickEvent().getAction().getName();
    }

    @Nullable
    public String getClickValue() {
        return base.getClickEvent() == null ? null : base.getClickEvent().getValue();
    }

    @Nullable
    public Runnable getCustomClickValue() {
        if (base.getClickEvent() == null) {
            return null;
        }
        if (base.getClickEvent() instanceof CustomClickEvent) {
            return ((CustomClickEvent) base.getClickEvent()).getEvent();
        }
        return null;
    }

    @DocletReplaceReturn("TextHoverAction | null")
    @Nullable
    public String getHoverAction() {
        return base.getHoverEvent() == null ? null : base.getHoverEvent().getAction().getName();
    }

    @Nullable
    public Object getHoverValue() {
        if (base.getHoverEvent() == null) {
            return null;
        }
        Object value = base.getHoverEvent().getValue(base.getHoverEvent().getAction());
        if (value instanceof Text) {
            return TextHelper.wrap((Text) value);
        }
        if (value instanceof HoverEvent.ItemStackContent) {
            return new ItemStackHelper(((HoverEvent.ItemStackContent) value).asStack());
        }
        if (value instanceof HoverEvent.EntityContent) {
            return ((HoverEvent.EntityContent) value).asTooltip().stream().map(TextHelper::wrap).collect(Collectors.toList());
        }
        return value;
    }

    public String getInsertion() {
        return base.getInsertion();
    }

    @Override
    public String toString() {
        return "StyleHelper:{\"color\": \"" + (hasColor() ? hasCustomColor() ? getCustomColor() : String.format("%x", getColorIndex()) : "none") + "\"" +
                ", \"bold\": " + bold() +
                ", \"italic\": " + italic() +
                ", \"underlined\": " + underlined() +
                ", \"strikethrough\": " + strikethrough() +
                ", \"obfuscated\": " + obfuscated() +
                ", \"clickAction\": \"" + getClickAction() + "\"" +
                ", \"clickValue\": \"" + getClickValue() + "\"" +
                ", \"customClickValue\": " + getCustomClickValue() +
                ", \"hoverAction\": \"" + getHoverAction() + "\"" +
                ", \"hoverValue\": " + getHoverValue() +
                ", \"insertion\": \"" + getInsertion() + "\"" +
                "}";
    }

}
