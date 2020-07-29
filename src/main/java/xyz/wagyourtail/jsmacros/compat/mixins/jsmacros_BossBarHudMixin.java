package xyz.wagyourtail.jsmacros.compat.mixins;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import xyz.wagyourtail.jsmacros.compat.interfaces.IBossBarHud;

@Mixin(BossBarHud.class)
public class jsmacros_BossBarHudMixin implements IBossBarHud {
    
    @Shadow
    @Final
    private Map<UUID, ClientBossBar> bossBars;

    @Override
    public Map<UUID, ClientBossBar> getBossBars() {
        return bossBars;
    }

    
}
