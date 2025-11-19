package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.helper.FormattingHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.StyleHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.net.URI;
import java.util.*;

/**
 * usage: {@code builder.append("hello,").withColor(0xc).append(" World!").withColor(0x6)}
 *
 * @author Wagyourtail
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class TextBuilder {
    private final MutableText head = Text.literal("");
    private MutableText self = head;

    public TextBuilder() {

    }

    /**
     * move on to next section and set it's text.
     *
     * @param text a {@link String}, {@link TextHelper} or {@link TextBuilder}
     * @return
     * @since 1.3.0
     */
    public TextBuilder append(Object text) {
        if (text instanceof TextHelper) {
            appendInternal((TextHelper) text);
        } else if (text instanceof TextBuilder) {
            appendInternal(((TextBuilder) text).build());
        } else {
            appendInternal(text.toString());
        }
        return this;
    }

    private void appendInternal(String text) {
        head.append(self = Text.literal(text));
    }

    private void appendInternal(TextHelper helper) {
        assert helper.getRaw() instanceof MutableText;
        head.append(self = (MutableText) helper.getRaw());
    }

    /**
     * set current section's color by color code as hex, like {@code 0x6} for gold
     * and {@code 0xc} for red.
     *
     * @param color
     * @return
     * @since 1.3.0
     */
    public TextBuilder withColor(int color) {
        self.styled(style -> style.withColor(Formatting.byColorIndex(color)));
        return this;
    }

    /**
     * Add text with custom colors.
     *
     * @param r red {@code 0-255}
     * @param g green {@code 0-255}
     * @param b blue {@code 0-255}
     * @return
     * @since 1.3.1
     */
    public TextBuilder withColor(int r, int g, int b) {
        self.styled(style -> style.withColor(TextColor.fromRgb((r & 255) << 16 | (g & 255) << 8 | (b & 255))));
        return this;
    }

    /**
     * set other formatting options for the current section
     *
     * @param underline
     * @param bold
     * @param italic
     * @param strikethrough
     * @param magic
     * @return
     * @since 1.3.0
     */
    public TextBuilder withFormatting(boolean underline, boolean bold, boolean italic, boolean strikethrough, boolean magic) {
        List<Formatting> formattings = new LinkedList<>();
        if (underline) {
            formattings.add(Formatting.UNDERLINE);
        }
        if (bold) {
            formattings.add(Formatting.BOLD);
        }
        if (italic) {
            formattings.add(Formatting.ITALIC);
        }
        if (strikethrough) {
            formattings.add(Formatting.STRIKETHROUGH);
        }
        if (magic) {
            formattings.add(Formatting.OBFUSCATED);
        }
        self.styled(style -> style.withFormatting(formattings.toArray(new Formatting[0])));
        return this;
    }

    /**
     * @param formattings the formattings to apply
     * @return self for chaining.
     * @since 1.8.4
     */
    public TextBuilder withFormatting(FormattingHelper... formattings) {
        self.styled(style -> style.withFormatting(Arrays.stream(formattings).map(FormattingHelper::getRaw).toArray(Formatting[]::new)));
        return this;
    }

    /**
     * set current section's hover event to show text
     *
     * @param text
     * @return
     * @since 1.3.0
     */
    public TextBuilder withShowTextHover(TextHelper text) {
        self.styled(style -> style.withHoverEvent(new HoverEvent.ShowText(text.getRaw())));
        return this;
    }

    /**
     * set current section's hover event to show an item
     *
     * @param item
     * @return
     * @since 1.3.0
     */
    public TextBuilder withShowItemHover(ItemStackHelper item) {
        self.styled(style -> style.withHoverEvent(new HoverEvent.ShowItem(item.getRaw())));
        return this;
    }

    /**
     * set current section's hover event to show an entity
     *
     * @param entity
     * @return
     * @since 1.3.0
     */
    public TextBuilder withShowEntityHover(EntityHelper<Entity> entity) {
        Entity raw = entity.getRaw();
        self.styled(style -> style.withHoverEvent(new HoverEvent.ShowEntity(new HoverEvent.EntityContent(raw.getType(), raw.getUuid(), raw.getName()))));
        return this;
    }

    /**
     * custom click event.
     *
     * @param action
     * @return
     * @since 1.3.0
     */
    public TextBuilder withCustomClickEvent(MethodWrapper<Object, Object, Object, ?> action) {
        self.styled(style -> style.withClickEvent(new CustomClickEvent(() -> {
            try {
                action.run();
            } catch (Throwable ex) {
                JsMacrosClient.clientCore.profile.logError(ex);
            }
        })));
        return this;
    }

    /**
     * normal click events like: {@code open_url}, {@code open_file}, {@code run_command}, {@code suggest_command}, {@code change_page}, and {@code copy_to_clipboard}
     *
     * @param action
     * @param value
     * @return
     * @since 1.3.0
     */
    @DocletReplaceParams("action: TextClickAction, value: string")
    public TextBuilder withClickEvent(String action, String value) {
        ClickEvent.Action clickAction = ClickEvent.Action.valueOf(action.toUpperCase(Locale.ROOT));
        Identifier id = Identifier.of(value);
        RegistryWrapper.WrapperLookup lookup = Objects
                .requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
                .getRegistryManager();
        self.styled(style -> style.withClickEvent(switch (clickAction) {
            case OPEN_URL -> new ClickEvent.OpenUrl(URI.create(value));
            case OPEN_FILE -> new ClickEvent.OpenFile(value);
            case RUN_COMMAND -> new ClickEvent.RunCommand(value);
            case SUGGEST_COMMAND -> new ClickEvent.SuggestCommand(value);
            case SHOW_DIALOG -> {
                var registryWrapper = lookup.getOrThrow(RegistryKeys.DIALOG);
                var dialogKey = RegistryKey.of(RegistryKeys.DIALOG, Identifier.of(value));
                var entry = registryWrapper.getOptional(dialogKey).orElseThrow(() -> new IllegalArgumentException("Unknown dialog type: " + value));
                yield new ClickEvent.ShowDialog(entry);
            }
            case CHANGE_PAGE -> new ClickEvent.ChangePage(Integer.parseInt(value));
            case COPY_TO_CLIPBOARD -> new ClickEvent.CopyToClipboard(value);
            case CUSTOM -> new ClickEvent.Custom(Identifier.of(value),null);
        }));
        return this;
    }

    public TextBuilder withStyle(StyleHelper style) {
        self.setStyle(style.getRaw());
        return this;
    }

    /**
     * @return the width of this text.
     * @since 1.8.4
     */
    public int getWidth() {
        return MinecraftClient.getInstance().textRenderer.getWidth(head);
    }

    /**
     * Build to a {@link TextHelper}
     *
     * @return
     * @since 1.3.0
     */
    public TextHelper build() {
        return TextHelper.wrap(head);
    }

}
