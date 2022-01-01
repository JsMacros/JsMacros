package xyz.wagyourtail.jsmacros.core.language;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Locale;

public final class TranslationUtil {

    private TranslationUtil() {
    }

    public static Text getTranslatedEventName(String eventName) {
        String lowerCaseName = eventName.toLowerCase(Locale.ROOT);
        return new LiteralText(I18n.hasTranslation("jsmacros.event." + lowerCaseName) ? I18n.translate("jsmacros.event." + lowerCaseName) : eventName);
    }

}
