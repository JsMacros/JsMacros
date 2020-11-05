package xyz.wagyourtail.jsmacros.gui.screens.editor.hilighting;

import net.minecraft.text.TextColor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class DefaultTheme extends TextTheme {
    private static Map<String, TextColor> theme = new HashMap<>();
    static {
        // JS
        theme.put("keyword", TextColor.fromRgb(0xCC7832));
        theme.put("number", TextColor.fromRgb(0x79ABFF));
        theme.put("function-variable", TextColor.fromRgb(0x79ABFF));
        theme.put("function", TextColor.fromRgb(0xA2EA22));
        theme.put("operator", TextColor.fromRgb(0xD8D8D8));
        theme.put("string", TextColor.fromRgb(0x12D489));
        theme.put("comment", TextColor.fromRgb(0xA0A0A0));
        theme.put("constant", TextColor.fromRgb(0x21B43E));
        theme.put("class-name", TextColor.fromRgb(0x21B43E));
        theme.put("boolean", TextColor.fromRgb(0xFFE200));
        theme.put("punctuation", TextColor.fromRgb(0xD8D8D8));
        theme.put("interpolation-punctuation", TextColor.fromRgb(0xCC7832));
        
        //py
        theme.put("builtin", TextColor.fromRgb(0x21B43E));
        theme.put("format-spec", TextColor.fromRgb(0xCC7832));
        
        //regex
        theme.put("regex", TextColor.fromRgb(0x12D489));
        theme.put("charset-negation", TextColor.fromRgb(0xCC7832));
        theme.put("charset-punctuation", TextColor.fromRgb(0xD8D8D8));
        theme.put("escape", TextColor.fromRgb(0xFFE200));
        theme.put("charclass", TextColor.fromRgb(0xFFE200));
        theme.put("quantifier", TextColor.fromRgb(0x79ABFF));
    }
    
    @Override
    @Nullable
    public TextColor getColorForToken(String name) {
        return theme.get(name);
    }
    
}
