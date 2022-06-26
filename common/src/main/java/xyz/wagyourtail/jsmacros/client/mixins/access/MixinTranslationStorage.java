package xyz.wagyourtail.jsmacros.client.mixins.access;


import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(TranslationStorage.class)
public class MixinTranslationStorage {

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;)Lnet/minecraft/client/resource/language/TranslationStorage;", locals = LocalCapture.CAPTURE_FAILHARD)
    private static void insertFabricLanguageData(ResourceManager p_239497_0_, List<LanguageDefinition> p_239497_1_, CallbackInfoReturnable<TranslationStorage> cir, Map<String, String> map) {
        Map<String, String> translations = new LinkedHashMap<>();
        for (LanguageDefinition lang : p_239497_1_) {
            Set<Map<String, String>> res = Core.getInstance().extensions.getAllExtensions().stream().map(e -> e.getTranslations(lang.getCode())).collect(Collectors.toSet());
            for (Map<String, String> r : res) {
                translations.putAll(r);
            }
        }
        translations.forEach(map::putIfAbsent);
    }
}
