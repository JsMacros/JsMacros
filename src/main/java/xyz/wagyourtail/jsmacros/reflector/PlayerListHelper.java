package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.client.network.PlayerListEntry;

public class PlayerListHelper {
    private PlayerListEntry p;
    
    public PlayerListHelper(PlayerListEntry p) {
        this.p = p;
    }
    
    public String getName() {
        return p.getDisplayName().asFormattedString();
    }
    
    public PlayerListEntry getRaw() {
        return p;
    }
}
