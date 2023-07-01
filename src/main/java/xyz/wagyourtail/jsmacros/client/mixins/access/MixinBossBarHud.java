package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IBossBarHud;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class MixinBossBarHud implements IBossBarHud {

    @Shadow
    @Final
    private Map<UUID, ClientBossBar> bossBars;

    @Override
    public Map<UUID, ClientBossBar> jsmacros_GetBossBars() {
        return bossBars;
    }

}
