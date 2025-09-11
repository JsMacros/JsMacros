package xyz.wagyourtail.jsmacros.client.api.helper;

import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Wagyourtail
 * @since 1.0.8
 */
@SuppressWarnings("unused")
public class TextHelper extends BaseHelper<Text> {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("\u00a7[0-9A-FK-OR]", Pattern.CASE_INSENSITIVE);

    private TextHelper(Text t) {
        super(t);
    }

    public static TextHelper wrap(Text t) {
        if (t != null) {
            return new TextHelper(t);
        } else {
            return null;
        }
    }

    /**
     * replace the text in this class with JSON data.
     *
     * @param json
     * @return
     * @since 1.0.8
     * @deprecated use {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FChat#createTextHelperFromJSON(String)} instead.
     */
    @Deprecated
    public String replaceFromJson(String json) {
        return "dont use this";
    }

    /**
     * replace the text in this class with {@link String String} data.
     *
     * @param content
     * @return
     * @since 1.0.8
     * @deprecated use {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FChat#createTextHelperFromString(String)} instead.
     */
    @Deprecated
    public TextHelper replaceFromString(String content) {
        base = Text.literal(content);
        return this;
    }

    /**
     * @return JSON data representation.
     * @since 1.2.7
     */
    public String getJson() {
        var registryManager = Objects.requireNonNull(mc.player).getRegistryManager();
        return String.valueOf(TextCodecs.CODEC.encodeStart(registryManager.getOps(JsonOps.INSTANCE), base).getOrThrow());
    }

    /**
     * @return the text content.
     * @since 1.2.7
     */
    public String getString() {
        return base.getString();
    }

    /**
     * @return the text content. stripped formatting when servers send it the (super) old way due to shitty coders.
     * @since 1.6.5
     */
    public String getStringStripFormatting() {
        return STRIP_FORMATTING_PATTERN.matcher(base.getString()).replaceAll("");
    }

    /**
     * @return the text helper without the formatting applied.
     * @since 1.8.4
     */
    public TextHelper withoutFormatting() {
        return TextHelper.wrap(Text.literal(getStringStripFormatting()));
    }

    /**
     * @param visitor function with 2 args, no return.
     * @since 1.6.5
     */
    public TextHelper visit(MethodWrapper<StyleHelper, String, Object, ?> visitor) {
        base.visit((style, string) -> {
            visitor.accept(new StyleHelper(style), string);
            return Optional.empty();
        }, base.getStyle());
        return this;
    }

    /**
     * @return the width of this text.
     * @since 1.8.4
     */
    public int getWidth() {
        return MinecraftClient.getInstance().textRenderer.getWidth(base);
    }

    /**
     * @return
     * @since 1.0.8
     * @deprecated confusing name, use {@link #getJson()} instead.
     */
    @Deprecated
    public String toJson() {
        return getJson();
    }

    /**
     * @return String representation of text helper.
     * @since 1.0.8, this used to do the same as {@link #getString}
     */
    @Override
    public String toString() {
        return String.format("TextHelper:{\"text\": \"%s\"}", base.getString());
    }

}
