package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.client.network.PlayerListEntry;

public class PlayerListEntryHelper {
    private PlayerListEntry p;
    
    public PlayerListEntryHelper(PlayerListEntry p) {
        this.p = p;
    }
    
    public String getName() {
        return p.getDisplayName().asString();
    }
    
    public PlayerListEntry getRaw() {
        return p;
    }
}
