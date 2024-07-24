package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventBossbar;

import java.util.UUID;

public class BossBarConsumer implements BossBarS2CPacket.Consumer {
    public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        ClientBossBar bar = MinecraftClient.getInstance().inGameHud.getBossBarHud().bossBars.get(uuid);
        new EventBossbar("ADD", uuid, bar).trigger();
    }

    public void remove(UUID uuid) {
        new EventBossbar("REMOVE", uuid, null).trigger();
    }

    public void updateProgress(UUID uuid, float percent) {
        ClientBossBar bar = MinecraftClient.getInstance().inGameHud.getBossBarHud().bossBars.get(uuid);
        new EventBossbar("UPDATE_PERCENT", uuid, bar).trigger();
    }

    public void updateName(UUID uuid, Text name) {
        ClientBossBar bar = MinecraftClient.getInstance().inGameHud.getBossBarHud().bossBars.get(uuid);
        new EventBossbar("UPDATE_NAME", uuid, bar).trigger();
    }

    public void updateStyle(UUID id, BossBar.Color color, BossBar.Style style) {
        ClientBossBar bar = MinecraftClient.getInstance().inGameHud.getBossBarHud().bossBars.get(id);
        new EventBossbar("UPDATE_STYLE", id, bar).trigger();
    }

    public void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        ClientBossBar bar = MinecraftClient.getInstance().inGameHud.getBossBarHud().bossBars.get(uuid);
        new EventBossbar("UPDATE_PROPERTIES", uuid, bar).trigger();
    }

}
