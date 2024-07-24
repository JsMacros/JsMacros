package xyz.wagyourtail.jsmacros.util;

import net.minecraft.text.Text;
import net.minecraft.util.Language;

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
        return Language.getInstance().hasTranslation("jsmacros.event." + lowerCaseName) ? Text.translatable("jsmacros.event." + lowerCaseName) : Text.literal(eventName);
    }

}
