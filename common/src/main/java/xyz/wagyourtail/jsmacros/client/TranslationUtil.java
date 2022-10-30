package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.Text;

import java.util.Locale;
import java.util.Objects;

public final class TranslationUtil {

    private TranslationUtil() {
    }

    public static Text getTranslatedEventName(String eventName) {
        String lowerCaseName = eventName.toLowerCase(Locale.ROOT);
        String translation = I18n.translate("jsmacros.event." + lowerCaseName);
        if (translation == null || translation.equals("jsmacros.event." + lowerCaseName)) {
            return new LiteralText(eventName);
        }
        return new TranslatableText("jsmacros.event." + lowerCaseName);
    }

}
