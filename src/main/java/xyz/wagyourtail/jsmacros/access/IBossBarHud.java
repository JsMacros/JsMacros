package xyz.wagyourtail.jsmacros.access;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.gui.hud.ClientBossBar;

public interface IBossBarHud {
    public Map<UUID, ClientBossBar> jsmacros_GetBossBars();
}
