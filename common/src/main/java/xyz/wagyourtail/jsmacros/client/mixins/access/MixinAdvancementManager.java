package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IAdvancementManager;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@Mixin(AdvancementManager.class)
public class MixinAdvancementManager implements IAdvancementManager {

    @Shadow
    @Final
    private Map<Identifier, Advancement> advancements;

    @Shadow
    @Final
    private Set<Advancement> dependents;

    @Override
    public Map<Identifier, Advancement> jsmacros_getAdvancementMap() {
        return Collections.unmodifiableMap(advancements);
    }

    @Override
    public Set<Advancement> jsmacros_getDependents() {
        return dependents;
    }
}
