package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.text.Text;

public class TextHelper {
    Text t;
    
    public TextHelper(String json) {
        t = Text.Serializer.fromJson(json);
    }
    
    public TextHelper(Text t) {
        this.t = t;
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
