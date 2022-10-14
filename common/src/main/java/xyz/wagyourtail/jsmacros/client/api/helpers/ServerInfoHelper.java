package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @since 1.6.5
 */
public class ServerInfoHelper extends BaseHelper<ServerData> {

    public ServerInfoHelper(ServerData base) {
        super(base);
    }

    public String getName() {
        return base.name;
    }

    public String getAddress() {
        return base.address;
    }

    public TextHelper getPlayerCountLabel() {
        return new TextHelper(new ChatComponentText(base.playerCountLabel));
    }

    public TextHelper getLabel() {
        return new TextHelper(new ChatComponentText(base.label));
    }

    public long getPing() {
        return base.ping;
    }

    public int getProtocolVersion() {
        return base.protocolVersion;
    }

    public TextHelper getVersion() {
        return new TextHelper(new ChatComponentText(base.version));
    }

    public String getPlayerListSummary() {
        return base.playerListSummary;
    }

    public String resourcePackPolicy() {
        return ((ChatComponentTranslation) base.getResourcePack().getName()).getKey();
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

    @Override
    public String toString() {
        return "ServerInfoHelper{" + getName() + ", " + getAddress() + "}";
    }

}
