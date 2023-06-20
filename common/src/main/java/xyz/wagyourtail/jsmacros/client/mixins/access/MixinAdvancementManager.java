package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AdvancementManager.class)
public interface MixinAdvancementManager {

    @Accessor
    Map<Identifier, Advancement> getAdvancements();

    @Accessor
    Set<Advancement> getDependents();

}
