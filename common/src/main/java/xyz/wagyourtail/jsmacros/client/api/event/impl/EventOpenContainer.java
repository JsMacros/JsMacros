package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.6.5
 */
@Event("OpenContainer")
public class EventOpenContainer implements BaseEvent {
    public final Inventory<?> inventory;
    public final IScreen screen;
    public boolean cancelled = false;

    public EventOpenContainer(HandledScreen screen) {
        this.inventory = Inventory.create(screen);
        this.screen = (IScreen) screen;
        profile.triggerEventJoinNoAnything(this);
    }
}
