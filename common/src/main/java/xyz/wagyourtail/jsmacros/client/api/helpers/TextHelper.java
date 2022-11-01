package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.function.BiConsumer;

import static xyz.wagyourtail.jsmacros.client.access.backports.TextBackport.literal;

/**
 * @author Wagyourtail
 * @since 1.0.8
 */
@SuppressWarnings("unused")
public class TextHelper extends BaseHelper<Text> {

    public static final Pattern STRIP_FORMATTING_PATTERN = Pattern.compile("\u00a7[0-9A-FK-OR]", Pattern.CASE_INSENSITIVE);
    
    public TextHelper(Text t) {
        super(t);
    }
    
    /**
     * replace the text in this class with JSON data.
     * @since 1.0.8
     * @param json
     * @deprecated use {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FChat#createTextHelperFromJSON(String)} instead.
     * @return
     */
    @Deprecated
    public TextHelper replaceFromJson(String json) {
        base = Text.Serializer.fromJson(json);
        return this;
    }
    
    /**
     * replace the text in this class with {@link java.lang.String String} data.
     * @since 1.0.8
     * @param content
     * @deprecated use {@link xyz.wagyourtail.jsmacros.client.api.library.impl.FChat#createTextHelperFromString(String)} instead.
     * @return
     */
    @Deprecated
    public TextHelper replaceFromString(String content) {
        base = new LiteralText(content);
        return this;
    }
    
    /**
     * @since 1.2.7
     * @return JSON data representation.
     */
    public String getJson() {
        return Text.Serializer.toJson(base);
    }

    /**
     * @since 1.2.7
     * @return the text content.
     */
    public String getString() {
        return base.getString();
    }

    /**
     * @since 1.6.5
     * @return the text content. stripped formatting when servers send it the (super) old way due to shitty coders.
     */
    public String getStringStripFormatting() {
        return STRIP_FORMATTING_PATTERN.matcher(base.getString()).replaceAll("");
    }

    /**
     * @return the text helper without the formatting applied.
     *
     * @since 1.8.4
     */
    public TextHelper withoutFormatting() {
        return new TextHelper(literal(getStringStripFormatting()));
    }
    
    /**
     * @param visitor function with 2 args, no return.
     * @since 1.6.5
     */
    public void visit(MethodWrapper<StyleHelper, String, Object, ?> visitor) {
        visit_internal(base, visitor);
    }

    private static void visit_internal(Text text, BiConsumer<StyleHelper, String> visitor) {
        visitor.accept(new StyleHelper(text.getStyle()), text.asString());
        for (Text sibling : text.getSiblings()) {
            visit_internal(sibling, visitor);
        }
    }

    /**
     * @return the width of this text.
     *
     * @since 1.8.4
     */
    public int getWidth() {
        return MinecraftClient.getInstance().textRenderer.getWidth(base);
    }
    
    /**
     * @since 1.0.8
     * @deprecated confusing name, use {@link #getJson()} instead.
     * @return
     */
     @Deprecated
    public String toJson() {
        return getJson();
    }

    /**
     * @since 1.0.8, this used to do the same as {@link #getString}
     * @return String representation of text helper.
     */
    @Override
    public String toString() {
        return String.format("TextHelper:{\"text\": \"%s\"}", base.getString());
    }
}
