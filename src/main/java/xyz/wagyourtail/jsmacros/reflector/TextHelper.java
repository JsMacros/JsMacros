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
    
    public void replaceFromJson(String json) {
        t = Text.Serializer.fromJson(json);
    }
    
    public void replaceFromString(String content) {
        t = new LiteralText(content);
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
