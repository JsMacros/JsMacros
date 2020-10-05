package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.jsMacros;

public interface IEvent {
    static final IProfile profile = jsMacros.profile;
    static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public default String getEventName() {
        return this.getClass().getSimpleName();
    }
}
