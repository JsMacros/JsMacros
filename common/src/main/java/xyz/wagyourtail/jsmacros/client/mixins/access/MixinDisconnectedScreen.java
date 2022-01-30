package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DisconnectedScreen.class)
public interface MixinDisconnectedScreen {

    @Accessor
    Text getReason();
}
