package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.wagyourtail.jsmacros.client.access.IResourcePackManager;

@Mixin(ResourcePackManager.class)
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

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePackProfile;isAlwaysEnabled()Z"), method = "buildEnabledProfiles")
    public boolean onBuildPackList(ResourcePackProfile instance) {
        if (instance.getName().equals("server")) {
            return instance.isAlwaysEnabled() && !disableServerPacks;
        }
        return instance.isAlwaysEnabled();
    }

}
