package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IHorseScreen;

@Mixin(HorseScreen.class)
public class MixinHorseScreen implements IHorseScreen {
    @Shadow
    @Final
    private HorseBaseEntity entity;

    @Override
    public Entity jsmacros_getEntity() {
        return entity;
    }
}
