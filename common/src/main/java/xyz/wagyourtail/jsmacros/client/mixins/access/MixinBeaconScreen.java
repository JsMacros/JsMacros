package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.entity.effect.StatusEffect;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IBeaconScreen;

@Mixin(BeaconScreen.class)
public class MixinBeaconScreen implements IBeaconScreen {

    @Shadow @Nullable StatusEffect primaryEffect;

    @Shadow @Nullable StatusEffect secondaryEffect;

    @Override
    public StatusEffect jsmacros_getPrimaryEffect() {
        return primaryEffect;
    }

    @Override
    public void jsmacros_setPrimaryEffect(StatusEffect effect) {
        primaryEffect = effect;
    }

    @Override
    public StatusEffect jsmacros_getSecondaryEffect() {
        return secondaryEffect;
    }

    @Override
    public void jsmacros_setSecondaryEffect(StatusEffect effect) {
        secondaryEffect = effect;
    }

}
