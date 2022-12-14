package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "Death", oldName = "DEATH")
public class EventDeath implements BaseEvent {

    public final BlockPosHelper deathPos;
    public final List<ItemStackHelper> inventory;
     
    public EventDeath() {
        this.deathPos = new BlockPosHelper(MinecraftClient.getInstance().player.getBlockPos());
        PlayerInventory inv = MinecraftClient.getInstance().player.getInventory();
        inventory = new ArrayList<>();
        for (int i = 0; i < inv.size(); i++) {
            this.inventory.add(new ItemStackHelper(inv.getStack(i)));
        }
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