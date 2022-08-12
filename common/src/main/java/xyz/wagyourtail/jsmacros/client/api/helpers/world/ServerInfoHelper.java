package xyz.wagyourtail.jsmacros.client.api.helpers.world;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.api.helpers.NBTElementHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @since 1.6.5
 */
@SuppressWarnings("unused")
public class ServerInfoHelper extends BaseHelper<ServerInfo> {

    public ServerInfoHelper(ServerInfo base) {
        super(base);
    }

    public String getName() {
        return base.name;
    }

    public String getAddress() {
        return base.address;
    }

    public TextHelper getPlayerCountLabel() {
        return new TextHelper(base.playerCountLabel);
    }

    public TextHelper getLabel() {
        return new TextHelper(base.label);
    }

    public long getPing() {
        return base.ping;
    }

    public int getProtocolVersion() {
        return base.protocolVersion;
    }

    public TextHelper getVersion() {
        return new TextHelper(base.version);
    }

    public List<TextHelper> getPlayerListSummary() {
        return base.playerListSummary.stream().map(TextHelper::new).collect(Collectors.toList());
    }

    public String resourcePackPolicy() {
        return ((TranslatableText) base.getResourcePackPolicy().getName()).getKey();
    }

    public String getIcon() {
        return getIcon();
    }

    public boolean isOnline() {
        return base.online;
    }

    public boolean isLocal() {
        return base.isLocal();
    }

    public NBTElementHelper<?> getNbt() {
        return NBTElementHelper.resolve(base.toNbt());
    }

    /**
     * @return {@code true} if the server enforces secure chat, {@code false} otherwise.
     *
     * @since 1.8.4
     */
    public boolean isSecureChatEnforced() {
        return false;
    }
    
    @Override
    public String toString() {
        return "ServerInfoHelper:{" + getNbt().asString() + "}";
    }

}
