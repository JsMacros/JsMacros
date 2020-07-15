package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import xyz.wagyourtail.jsmacros.compat.interfaces.IHorseScreen;

@Mixin(HorseScreen.class)
public class jsmacros_HorseScreenMixin implements IHorseScreen {
    @Shadow
    @Final
    private HorseBaseEntity entity;

    @Override
    public Entity getEntity() {
        return entity;
    }
}
