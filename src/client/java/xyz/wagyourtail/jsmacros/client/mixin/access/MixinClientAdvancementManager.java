package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(ClientAdvancementManager.class)
public interface MixinClientAdvancementManager {

    @Accessor
    Map<AdvancementEntry, AdvancementProgress> getAdvancementProgresses();

}
