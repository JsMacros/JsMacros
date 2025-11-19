package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * This event is fired after resources have been reloaded, i.e. after the splash screen has finished.
 * This includes when the game is finished loading and the title screen becomes visible, which you can check using
 * {@link #isGameStart}.
 *
 * @since 1.5.1
 */
@Event("ResourcePackLoaded")
public class EventResourcePackLoaded extends BaseEvent {
    public final boolean isGameStart;
    public final List<String> loadedPacks;

    public EventResourcePackLoaded(boolean isGameStart) {
        super(JsMacrosClient.clientCore);
        this.isGameStart = isGameStart;
        this.loadedPacks = new ArrayList<>(MinecraftClient.getInstance().getResourcePackManager().getEnabledIds());
    }

    @Override
    public String toString() {
        return String.format("%s:{\"isGameStart\": %b, \"loadedPacks\": %s}", this.getEventName(), isGameStart, loadedPacks);
    }

}
