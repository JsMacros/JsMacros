package xyz.wagyourtail.jsmacros.client.mixins.access;


import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.core.Core;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(TranslationStorage.class)
public class MixinTranslationStorage {

    @Shadow @Final protected Map<String, String> translations;

    @Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void insertFabricLanguageData(ResourceManager container, List<String> list, CallbackInfo ci) {
        Map<String, String> translations = new HashMap<>();
        for (String lang : list) {
            Set<Map<String, String>> res = Core.getInstance().extensions.getAllExtensions().stream().map(e -> e.getTranslations(lang)).collect(Collectors.toSet());
            for (Map<String, String> r : res) {
                translations.putAll(r);
            }
        }
        translations.forEach(this.translations::putIfAbsent);
    }
}
