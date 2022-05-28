package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandRegistryAccess.class)
public class MixinCommandRegistryAccess {

    @Shadow @Final private DynamicRegistryManager dynamicRegistryManager;

    @Redirect(method = "createWrapper", at = @At(value = "FIELD", target = "Lnet/minecraft/command/CommandRegistryAccess;dynamicRegistryManager:Lnet/minecraft/util/registry/DynamicRegistryManager;"))
    public DynamicRegistryManager onWrapper(CommandRegistryAccess instance) {
        if (dynamicRegistryManager == null) {
            return MinecraftClient.getInstance().getNetworkHandler().getRegistryManager();
        }
        return dynamicRegistryManager;
    }
}
