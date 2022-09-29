package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3i;

import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.Optional;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Death", oldName = "DEATH")
public class EventDeath implements BaseEvent {

    public final BlockPosHelper deathPos;
     
    public EventDeath() {
        this.deathPos = new BlockPosHelper(MinecraftClient.getInstance().player.getBlockPos());
        profile.triggerEvent(this);
    }

    /**
     * Respawns the player. Should be used with some delay, one tick should be enough.
     *
     * @since 1.8.5
     */
    public void respawn() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (!player.isAlive()) {
            player.requestRespawn();
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s:{\"deathPos\": %s}", this.getEventName(), deathPos);
    }
}
