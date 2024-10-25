package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPlayerListHud;

@Mixin(PlayerListHud.class)
public class MixinPlayerListHud implements IPlayerListHud {

    @Shadow
    private Text header;

    @Shadow
    private Text footer;

    @Override
    public Text jsmacros_getHeader() {
        return this.header;
    }

    @Override
    public Text jsmacros_getFooter() {
        return this.footer;
    }

}
