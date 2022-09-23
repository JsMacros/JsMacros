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

    
    public EventDeath() {
        
        profile.triggerEvent(this);
    }

    public void respawn() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (!player.isAlive()) {
            player.requestRespawn();
        }
    }

    public BlockPosHelper getLastDeathPos() {
        return MinecraftClient.getInstance().player.getLastDeathPos().map(pos -> new BlockPosHelper(pos.getPos())).orElse(null);
    }
    
    @Override
    public String toString() {
        return String.format("%s:{\"deathPos\": %s}", this.getEventName(), getLastDeathPos());
    }
}
