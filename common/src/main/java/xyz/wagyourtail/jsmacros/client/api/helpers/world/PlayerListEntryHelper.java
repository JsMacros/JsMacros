package xyz.wagyourtail.jsmacros.client.api.helpers.world;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.world.GameMode;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.0.2
 */
@SuppressWarnings("unused")
public class PlayerListEntryHelper extends BaseHelper<PlayerListEntry> {
    
    public PlayerListEntryHelper(PlayerListEntry p) {
        super(p);
    }
    
    /**
     * @since 1.1.9
     * @return
     */
    public String getUUID() {
        GameProfile prof = base.getProfile();
        return prof == null ? null : prof.getId().toString();
    }
    
    /**
     * @since 1.0.2
     * @return
     */
    public String getName() {
        GameProfile prof = base.getProfile();
        return prof == null ? null : prof.getName();
    }

    /**
     * @since 1.6.5
     * @return
     */
    public int getPing() {
        return base.getLatency();
    }

    /**
     * @since 1.6.5
     * @return #Gamemode# null if unknown
     */
    public String getGamemode() {
        GameMode gm = base.getGameMode();
        return gm == null ? null : gm.getName();
    }

    /**
     * @since 1.1.9
     * @return
     */
    public TextHelper getDisplayText() {
        return new TextHelper(base.getDisplayName());
    }

    /**
     * @since 1.8.2
     * @return
     */
    public byte[] getPublicKey() {
        return base.getSession().publicKeyData().data().key().getEncoded();
    }

    /**
     * @return {@code true} if the player has a cape enabled, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasCape() {
        return base.hasCape();
    }

    /**
     * A slim skin is an Alex skin, while the default one is Steve.
     *
     * @return {@code true} if the player has a slim skin, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean hasSlimModel() {
        return base.getModel().equals("slim");
    }

    /**
     * @return the identifier of the player's skin texture or {@code null} if it's unknown.
     *
     * @since 1.8.4
     */
    public String getSkinTexture() {
        return base.getSkinTexture() == null ? null : base.getSkinTexture().toString();
    }

    /**
     * @return the identifier of the player's cape texture or {@code null} if it's unknown.
     *
     * @since 1.8.4
     */
    public String getCapeTexture() {
        return base.getCapeTexture() == null ? null : base.getCapeTexture().toString();
    }

    /**
     * @return the identifier of the player's elytra texture or {@code null} if it's unknown.
     *
     * @since 1.8.4
     */
    public String getElytraTexture() {
        return base.getElytraTexture() == null ? null : base.getElytraTexture().toString();
    }
    
    /**
     * @return the team of the player or {@code null} if the player is not in a team.
     *
     * @since 1.8.4
     */
    public TeamHelper getTeam() {
        return base.getScoreboardTeam() == null ? null : new TeamHelper(base.getScoreboardTeam());
    }

    @Override
    public String toString() {
        return String.format("PlayerListEntryHelper:{\"uuid\": \"%s\", \"name\": \"%s\"}", this.getUUID(), this.getName());
    }
}
