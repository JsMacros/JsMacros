package xyz.wagyourtail.jsmacros.api.helpers;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

/**
 * @author Wagyourtail
 * @since 1.0.8
 */
public class TextHelper {
    Text t;
    
    public TextHelper(String json) {
        t = Text.Serializer.fromJson(json);
    }
    
    public TextHelper(Text t) {
        this.t = t;
    }
    
    /**
     * replace the text in this class with JSON data.
     * @since 1.0.8
     * @param json
     * @return
     */
    public TextHelper replaceFromJson(String json) {
        t = Text.Serializer.fromJson(json);
        return this;
    }
    
    /**
     * replace the text in this class with {@link java.lang.String String} data.
     * @since 1.0.8
     * @param json
     * @return
     */
    public TextHelper replaceFromString(String content) {
        t = new LiteralText(content);
        return this;
    }
    
    /**
     * @since 1.2.7
     * @return JSON data representation.
     */
    public String getJson() {
        return Text.Serializer.toJson(t);
    }

    /**
     * @since 1.2.7
     * @return the text content.
     */
    public String getString() {
        return t.getString();
    }
    
    
    /**
     * @since 1.0.8
     * @deprecated confusing name.
     * @return
     */
    public String toJson() {
        return getJson();
    }

    /**
     * @since 1.0.8, this used to do the same as getString
     * @return String representation of text helper.
     */
    public String toString() {
        return String.format("TextHelper:{\"text\": \"%s\"}", t.getString());
    }
    
    public Text getRaw() {
        return t;
    }
}
