package xyz.wagyourtail.jsmacros.client.backport;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TextBackport {

    public static Text empty() {
        return LiteralText.EMPTY;
    }

    public static MutableText literal(String text) {
        return new LiteralText(text);
    }

    public static MutableText translatable(String text, Object... args) {
        return new TranslatableText(text, args);
    }

}
