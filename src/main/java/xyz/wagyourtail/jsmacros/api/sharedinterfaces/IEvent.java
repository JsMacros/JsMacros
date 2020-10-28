package xyz.wagyourtail.jsmacros.api.sharedinterfaces;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.JsMacros;

public interface IEvent {
    static final IProfile profile = JsMacros.profile;
    static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public default String getEventName() {
        return this.getClass().getSimpleName();
    }
}
