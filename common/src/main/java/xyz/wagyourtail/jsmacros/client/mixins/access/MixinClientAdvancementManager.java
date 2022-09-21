package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientAdvancementManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IClientAdvancementManager;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(ClientAdvancementManager.class)
public class MixinClientAdvancementManager implements IClientAdvancementManager {
    @Shadow
    @Final
    private Map<Advancement, AdvancementProgress> advancementProgresses;

    @Override
    public Map<Advancement, AdvancementProgress> jsmacros_getAdvancementProgress() {
        return advancementProgresses;
    }
}
