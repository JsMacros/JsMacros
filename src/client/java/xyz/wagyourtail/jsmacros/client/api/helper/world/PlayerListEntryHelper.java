package xyz.wagyourtail.jsmacros.client.api.helper.world;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
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
     * @return
     * @since 1.1.9
     */
    @Nullable
    public String getUUID() {
        GameProfile prof = base.getProfile();
        return prof == null ? null : prof.getId().toString();
    }

    /**
     * @return
     * @since 1.0.2
     */
    @Nullable
    public String getName() {
        GameProfile prof = base.getProfile();
        return prof == null ? null : prof.getName();
    }

    /**
     * @return
     * @since 1.6.5
     */
    public int getPing() {
        return base.getLatency();
    }

    /**
     * @return null if unknown
     * @since 1.6.5
     */
    @DocletReplaceReturn("Gamemode")
    @Nullable
    public String getGamemode() {
        GameMode gm = base.getGameMode();
        return gm == null ? null : gm.getId();
    }

    /**
     * @return
     * @since 1.1.9
     */
    public TextHelper getDisplayText() {
        return TextHelper.wrap(base.getDisplayName());
    }

    /**
     * @return
     * @since 1.8.2
     */
    public byte[] getPublicKey() {
        return base.getSession().publicKeyData().data().key().getEncoded();
    }

    /**
     * @return {@code true} if the player has a cape enabled, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasCape() {
        return base.getSkinTextures().capeTexture() != null;
    }

    /**
     * A slim skin is an Alex skin, while the default one is Steve.
     *
     * @return {@code true} if the player has a slim skin, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean hasSlimModel() {
        return base.getSkinTextures().model().equals(SkinTextures.Model.SLIM);
    }

    /**
     * @return the identifier of the player's skin texture or {@code null} if it's unknown.
     * @since 1.8.4
     */
    public String getSkinTexture() {
        return base.getSkinTextures().texture().toString();
    }

    /**
     * @since 1.9.0
     */
    @Nullable
    public String getSkinUrl() {
        return base.getSkinTextures().textureUrl();
    }

    /**
     * @return the identifier of the player's cape texture or {@code null} if it's unknown.
     * @since 1.8.4
     */
    @Nullable
    public String getCapeTexture() {
        return base.getSkinTextures().capeTexture() == null ? null : base.getSkinTextures().capeTexture().toString();
    }

    /**
     * @return the identifier of the player's elytra texture or {@code null} if it's unknown.
     * @since 1.8.4
     */
    @Nullable
    public String getElytraTexture() {
        return base.getSkinTextures().elytraTexture() == null ? null : base.getSkinTextures().elytraTexture().toString();
    }

    /**
     * @return the team of the player or {@code null} if the player is not in a team.
     * @since 1.8.4
     */
    @Nullable
    public TeamHelper getTeam() {
        return base.getScoreboardTeam() == null ? null : new TeamHelper(base.getScoreboardTeam());
    }

    @Override
    public String toString() {
        return String.format("PlayerListEntryHelper:{\"uuid\": \"%s\", \"name\": \"%s\"}", this.getUUID(), this.getName());
    }

}
