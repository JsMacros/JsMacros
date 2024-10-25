package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.client.gui.screen.ingame.LoomScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ILoomScreen;

@Mixin(LoomScreen.class)
public class MixinLoomScreen implements ILoomScreen {

    @Shadow
    private boolean canApplyDyePattern;

    @Override
    public boolean jsmacros_canApplyDyePattern() {
        return canApplyDyePattern;
    }

}
