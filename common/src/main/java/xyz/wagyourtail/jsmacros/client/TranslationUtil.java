package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.util.Locale;
import java.util.Objects;

public final class TranslationUtil {

    private TranslationUtil() {
    }

    public static IChatComponent getTranslatedEventName(String eventName) {
        String lowerCaseName = eventName.toLowerCase(Locale.ROOT);
        String translation = I18n.translate("jsmacros.event." + lowerCaseName);
        if (translation == null || translation.equals("jsmacros.event." + lowerCaseName)) {
            return new ChatComponentText(eventName);
        }
        return new ChatComponentTranslation("jsmacros.event." + lowerCaseName);
    }

}
