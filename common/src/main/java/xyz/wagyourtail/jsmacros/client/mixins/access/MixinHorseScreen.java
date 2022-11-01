package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.class_3136;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IHorseScreen;

@Mixin(HorseScreen.class)
public class MixinHorseScreen implements IHorseScreen {
    @Shadow
    private class_3136 field_15252;

    @Override
    public Entity jsmacros_getEntity() {
        return field_15252;
    }
}
