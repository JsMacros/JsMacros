package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
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

    public int getColor() {
        if (base.getColor() == null) return -1;
        Formatting f = Formatting.byName(base.getColor().getName());
        if (f == null) return -1;
        return f.getColorIndex();
    }

    public boolean hasCustomColor() {
        if (base.getColor() == null) return false;
        return base.getColor().getName().startsWith("#");
    }

    public int getCustomColor() {
        if (base.getColor() == null) return -1;
        return base.getColor().getRgb();
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

    public String getClickAction() {
        if (base.getClickEvent() == null) return null;
        if (base.getClickEvent() instanceof CustomClickEvent) {
            return "custom";
        }
        return base.getClickEvent().getAction().getName();
    }

    public String getClickValue() {
        if (base.getClickEvent() == null) return null;
        return base.getClickEvent().getValue();
    }

    public Runnable getCustomClickValue() {
        if (base.getClickEvent() == null) return null;
        if (base.getClickEvent() instanceof CustomClickEvent) {
            return ((CustomClickEvent) base.getClickEvent()).getEvent();
        }
        return null;
    }

    public String getHoverAction() {
        if (base.getHoverEvent() == null) return null;
        return base.getHoverEvent().getAction().getName();
    }

    public Object getHoverValue() {
        if (base.getHoverEvent() == null) return null;
        Object value = base.getHoverEvent().getValue(base.getHoverEvent().getAction());
        if (value instanceof Text) return new TextHelper((Text) value);
        if (value instanceof HoverEvent.ItemStackContent) return new ItemStackHelper(((HoverEvent.ItemStackContent) value).asStack());
        if (value instanceof HoverEvent.EntityContent) return ((HoverEvent.EntityContent) value).asTooltip().stream().map(TextHelper::new).collect(Collectors.toList());
        return value;
    }

    public String getInsertion() {
        return base.getInsertion();
    }

    @Override
    public String toString() {
        return "StyleHelper{\"color\": \"" + (hasColor() ? hasCustomColor() ? getCustomColor() : String.format("%x", getColor()) : "none") + "\"" +
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
