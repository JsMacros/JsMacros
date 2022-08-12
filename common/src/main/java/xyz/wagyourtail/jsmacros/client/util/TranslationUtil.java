package xyz.wagyourtail.jsmacros.client.util;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Locale;

/**
 * @author Etheradon
 * @since 1.6.4
 */
public final class TranslationUtil {

    private TranslationUtil() {
    }

    public static Text getTranslatedEventName(String eventName) {
        String lowerCaseName = eventName.toLowerCase(Locale.ROOT);
        return I18n.hasTranslation("jsmacros.event." + lowerCaseName) ? new TranslatableText("jsmacros.event." + lowerCaseName) : new LiteralText(eventName);
    }

}
