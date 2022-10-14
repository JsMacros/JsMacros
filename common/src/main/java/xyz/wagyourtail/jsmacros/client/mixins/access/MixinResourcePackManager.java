package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IResourcePackManager;

@Mixin(ResourcePackRepository.class)
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

    @Inject(at = @At("HEAD"), method = "func_148530_e", cancellable = true)
    public void onBuildPackList(CallbackInfoReturnable<IResourcePack> cir) {
        if (disableServerPacks) {
            cir.setReturnValue(null);
        }
    }
}
