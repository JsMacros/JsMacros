package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @since 1.6.5
 */
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
        return new TextHelper(new LiteralText(base.playerCountLabel));
    }

    public TextHelper getLabel() {
        return new TextHelper(new LiteralText(base.label));
    }

    public long getPing() {
        return base.ping;
    }

    public int getProtocolVersion() {
        return base.protocolVersion;
    }

    public TextHelper getVersion() {
        return new TextHelper(new LiteralText(base.version));
    }

    public String getPlayerListSummary() {
        return base.playerListSummary;
    }

    public String resourcePackPolicy() {
        return ((TranslatableText) base.getResourcePack().getName()).getKey();
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
