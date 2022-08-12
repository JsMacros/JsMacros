package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Locale;

public final class TranslationUtil {

    private TranslationUtil() {
    }

    public static Text getTranslatedEventName(String eventName) {
        String lowerCaseName = eventName.toLowerCase(Locale.ROOT);
        return I18n.hasTranslation("jsmacros.event." + lowerCaseName) ? new TranslatableText("jsmacros.event." + lowerCaseName) : new LiteralText(eventName);
    }

}
