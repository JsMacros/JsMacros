package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class TextHelper {
    Text t;
    
    public TextHelper(String json) {
        t = Text.Serializer.fromJson(json);
    }
    
    public TextHelper(Text t) {
        this.t = t;
    }
    
    public TextHelper replaceFromJson(String json) {
        t = Text.Serializer.fromJson(json);
        return this;
    }
    
    public TextHelper replaceFromString(String content) {
        t = new LiteralText(content);
        return this;
    }
    
    public String toJson() {
        return Text.Serializer.toJson(t);
    }
    
    public String toString() {
        return t.getString();
    }
    
    public Text getRaw() {
        return t;
    }
}
