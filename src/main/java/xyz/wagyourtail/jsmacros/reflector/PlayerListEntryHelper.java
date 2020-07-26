package xyz.wagyourtail.jsmacros.reflector;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.PlayerListEntry;

public class PlayerListEntryHelper {
    private PlayerListEntry p;
    
    public PlayerListEntryHelper(PlayerListEntry p) {
        this.p = p;
    }
    
    public String getUUID() {
        GameProfile prof = p.getProfile();
        if (prof == null) return null;
        return prof.getId().toString();
    }
    
    public String getName() {
        GameProfile prof = p.getProfile();
        if (prof == null) return null;
        return prof.getName();
    }
    
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
