package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@Mixin(AnvilScreen.class)
public interface MixinAnvilScreen {

    @Accessor
    TextFieldWidget getNameField();

}
