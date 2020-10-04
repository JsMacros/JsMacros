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
     * @param json
     * @return
     */
    public TextHelper replaceFromJson(String json) {
        t = Text.Serializer.fromJson(json);
        return this;
    }
    
    /**
     * replace the text in this class with {@link java.lang.String String} data.
     * @param json
     * @return
     */
    public TextHelper replaceFromString(String content) {
        t = new LiteralText(content);
        return this;
    }
    
    /**
     * @return JSON data representation.
     */
    public String toJson() {
        return Text.Serializer.toJson(t);
    }
    
    /**
     * @return String representation.
     */
    public String toString() {
        return t.getString();
    }
    
    public Text getRaw() {
        return t;
    }
}
