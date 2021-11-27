package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.Map;
import java.util.UUID;

public interface IBossBarHud {
    Map<UUID, ClientBossBar> jsmacros_GetBossBars();
}
