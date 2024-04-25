package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IBeaconScreen;

@Mixin(BeaconScreen.class)
public class MixinBeaconScreen implements IBeaconScreen {

    @Shadow
    @Nullable
    private RegistryEntry<StatusEffect> primaryEffect;

    @Shadow
    @Nullable
    private RegistryEntry<StatusEffect> secondaryEffect;

    @Override
    public RegistryEntry<StatusEffect> jsmacros_getPrimaryEffect() {
        return primaryEffect;
    }

    @Override
    public void jsmacros_setPrimaryEffect(RegistryEntry<StatusEffect> effect) {
        primaryEffect = effect;
    }

    @Override
    public RegistryEntry<StatusEffect> jsmacros_getSecondaryEffect() {
        return secondaryEffect;
    }

    @Override
    public void jsmacros_setSecondaryEffect(RegistryEntry<StatusEffect> effect) {
        secondaryEffect = effect;
    }

}
