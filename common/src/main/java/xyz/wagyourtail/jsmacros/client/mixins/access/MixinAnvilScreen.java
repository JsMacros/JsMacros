package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;

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
    private TextFieldWidget nameField;

    @Override
    public TextFieldWidget jsmacros_getRenameText() {
        return nameField;
    }
}
