package xyz.wagyourtail.jsmacros.client.mixins.access;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.JsMacrosJsonLangFile;
import xyz.wagyourtail.jsmacros.core.Core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(Locale.class)
public class MixinTranslationStorage {

    @Shadow Map<String, String> translations;

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), method = {"method_4415", "func_135022_a"}, locals = LocalCapture.CAPTURE_FAILHARD)
    private void insertFabricLanguageData(IResourceManager container, List<String> list, CallbackInfo ci) {
        Map<String, String> translations = new HashMap<>();
        if (Core.getInstance() == null) {
            return;
        }
        for (String lang : list) {
            Set<Map<String, String>> res = JsMacros.core.extensions.getAllExtensions().stream().map(e -> e.getTranslations(lang)).collect(Collectors.toSet());
            for (Map<String, String> r : res) {
                translations.putAll(r);
            }

            Set<String> res2 = JsMacrosJsonLangFile.getLangResources(lang);
            for (String r : res2) {
                JsonObject ts = null;
                try (Reader reader = new InputStreamReader(JsMacrosJsonLangFile.class.getResourceAsStream(r))) {
                    ts = new JsonParser().parse(reader).getAsJsonObject();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ts.entrySet().forEach((e) -> translations.putIfAbsent(e.getKey(), e.getValue().getAsString()));
            }
        }
        translations.forEach(this.translations::putIfAbsent);
    }
}
