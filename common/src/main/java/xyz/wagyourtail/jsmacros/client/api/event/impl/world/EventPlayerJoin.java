package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import net.minecraft.client.network.PlayerListEntry;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.PlayerListEntryHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.UUID;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "PlayerJoin", oldName = "PLAYER_JOIN")
public class EventPlayerJoin implements BaseEvent {
    public final String UUID;
    public final PlayerListEntryHelper player;
    
    public EventPlayerJoin(UUID uuid, PlayerListEntry player) {
        this.UUID = uuid.toString();
        this.player = new PlayerListEntryHelper(player);
        
        profile.triggerEvent(this);
    }
    
    @Override
    public String toString() {
        return String.format("%s:{\"player\": %s}", this.getEventName(), player.toString());
    }
}
