package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IAnvilScreen;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AnvilScreen.class)
public abstract class MixinAnvilScreen implements IAnvilScreen {

    @Shadow
    protected abstract void onRenamed(String name);

    @Override
    public void jsmacros_rename(String name) {
        onRenamed(name);
    }

}
