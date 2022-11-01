package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.resource.ResourcePackLoader;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IResourcePackManager;

@Mixin(ResourcePackLoader.class)
public class MixinResourcePackManager implements IResourcePackManager {

    @Unique
    private boolean disableServerPacks = false;

    @Override
    public void jsmacros_disableServerPacks(boolean disable) {
        disableServerPacks = disable;
    }

    @Override
    public boolean jsmacros_isServerPacksDisabled() {
        return disableServerPacks;
    }

    @Inject(at = @At("HEAD"), method = "method_7039", cancellable = true)
    public void onBuildPackList(CallbackInfoReturnable<ResourcePack> cir) {
        if (disableServerPacks) {
            cir.setReturnValue(null);
        }
    }
}
