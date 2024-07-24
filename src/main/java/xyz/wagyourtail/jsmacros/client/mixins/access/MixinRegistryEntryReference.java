package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;

@Mixin(RegistryEntry.Reference.class)
public class MixinRegistryEntryReference {

    @Inject(method = "ownerEquals", at = @At("RETURN"), cancellable = true)
    public void overrideOwnerEquals(RegistryEntryOwner<?> owner, CallbackInfoReturnable<Boolean> cir) {
        if (owner == RegistryHelper.ALL_EQUALITY_OWNER) {
            cir.setReturnValue(true);
        }
    }

}
