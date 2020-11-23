package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;

/**
 * @author Wagyourtail
 * @since 1.0.2
 */
public class PlayerListEntryHelper {
    private PlayerListEntry p;
    
    public PlayerListEntryHelper(PlayerListEntry p) {
        this.p = p;
    }
    
    /**
     * @since 1.1.9
     * @return
     */
    public String getUUID() {
        GameProfile prof = p.getProfile();
        if (prof == null) return null;
        return prof.getId().toString();
    }
    
    /**
     * @since 1.0.2
     * @return
     */
    public String getName() {
        GameProfile prof = p.getProfile();
        if (prof == null) return null;
        return prof.getName();
    }
    
    /**
     * @since 1.1.9
     * @return
     */
    public TextHelper getDisplayText() {
        return new TextHelper(p.getDisplayName());
    }
    
    public PlayerListEntry getRaw() {
        return p;
    }
    
    public String toString() {
        return String.format("Player:{\"uuid\": \"%s\", \"name\":\"%s\"}", this.getUUID(), this.getName());
    }
}
