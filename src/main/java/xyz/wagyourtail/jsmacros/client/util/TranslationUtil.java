package xyz.wagyourtail.jsmacros.client.util;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.util.Locale;

import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.literal;
import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.translatable;

/**
 * @author Etheradon
 * @since 1.6.4
 */
public final class TranslationUtil {

    private TranslationUtil() {
    }

    public static Text getTranslatedEventName(String eventName) {
        String lowerCaseName = eventName.toLowerCase(Locale.ROOT);
        return I18n.hasTranslation("jsmacros.event." + lowerCaseName) ? translatable("jsmacros.event." + lowerCaseName) : literal(eventName);
    }

}
