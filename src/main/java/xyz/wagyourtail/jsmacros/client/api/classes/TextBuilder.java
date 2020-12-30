package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.entity.Entity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.LinkedList;
import java.util.List;


/**
 * usage: {@code builder.append("hello,").withColor(0xc).append(" World").withColor(0x6)}
 * @author Wagyourtail
 * @since 1.3.0
 */
public class TextBuilder {
    private final LiteralText head = new LiteralText("");
    private MutableText self = head;
    
    public TextBuilder() {
    
    }
    
    /**
     * move on to next section and set it's text.
     * @param text
     * @since 1.3.0
     * @return
     */
    public TextBuilder append(String text) {
        head.append(self = new LiteralText(text));
        return this;
    }
    
    /**
     * move on to next section and set it's text.
     * @param helper
     * @since 1.3.0
     * @return
     */
    public TextBuilder append(TextHelper helper) {
        assert helper.getRaw() instanceof MutableText;
        head.append(self = (MutableText) helper.getRaw());
        return this;
    }
    
    /**
     * set current section's color
     * @param color
     * @since 1.3.0
     * @return
     */
    public TextBuilder withColor(int color) {
        self.setStyle(self.getStyle().withColor(Formatting.byColorIndex(color)));
        return this;
    }
    
    /**
     * set other formatting options for the current section
     * @param underline
     * @param bold
     * @param italic
     * @param strikethrough
     * @param magic
     * @since 1.3.0
     * @return
     */
    public TextBuilder withFormatting(boolean underline, boolean bold, boolean italic, boolean strikethrough, boolean magic) {
        List<Formatting> formattings = new LinkedList<>();
        if (underline) formattings.add(Formatting.UNDERLINE);
        if (bold) formattings.add(Formatting.BOLD);
        if (italic) formattings.add(Formatting.ITALIC);
        if (strikethrough) formattings.add(Formatting.STRIKETHROUGH);
        if (magic) formattings.add(Formatting.OBFUSCATED);
        self.setStyle(self.getStyle().withFormatting(formattings.toArray(new Formatting[0])));
        return this;
    }
    
    /**
     * set current section's hover event to show text
     * @param text
     * @since 1.3.0
     * @return
     */
    public TextBuilder withShowTextHover(TextHelper text) {
        self.setStyle(self.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.getRaw())));
        return this;
    }
    
    /**
     * set current section's hover event to show an item
     * @param item
     * @since 1.3.0
     * @return
     */
    public TextBuilder withShowItemHover(ItemStackHelper item) {
        self.setStyle(self.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(item.getRaw()))));
        return this;
    }
    
    /**
     * set current section's hover event to show an entity
     * @param entity
     * @since 1.3.0
     * @return
     */
    public TextBuilder withShowEntityHover(EntityHelper<Entity> entity) {
        Entity raw = entity.getRaw();
        self.setStyle(self.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(raw.getType(), raw.getUuid(), raw.getName()))));
        return this;
    }
    
    /**
     * custom click event.
     * @param action
     * @since 1.3.0
     * @return
     */
    public TextBuilder withCustomClickEvent(MethodWrapper<Object, Object, Object> action) {
        self.setStyle(self.getStyle().withClickEvent(new CustomClickEvent(action)));
        return this;
    }
    
    /**
     * normal click events like: {@code open_url}, {@code open_file}, {@code run_command}, {@code suggest_command}, {@code change_page}, and {@code copy_to_clipboard}
     * @param action
     * @param value
     * @since 1.3.0
     * @return
     */
    public TextBuilder withClickEvent(String action, String value) {
        ClickEvent.Action clickAction = ClickEvent.Action.byName(action);
        assert action != null;
        self.setStyle(self.getStyle().withClickEvent(new ClickEvent(clickAction, value)));
        return this;
    }
    
    /**
     * Build to a {@link TextHelper}
     * @since 1.3.0
     * @return
     */
    public TextHelper build() {
        return new TextHelper(head);
    }
}
