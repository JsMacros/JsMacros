package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.ICancelable;

/**
 * @author Wagyourtail
 * @since 1.6.5
 */
@Event("OpenContainer")
public class EventOpenContainer implements BaseEvent, ICancelable {
    public final Inventory<?> inventory;
    public final IScreen screen;
    public boolean cancelled = false;

    public EventOpenContainer(HandledScreen<?> screen) {
        this.inventory = Inventory.create(screen);
        this.screen = (IScreen) screen;
        profile.triggerEventJoinNoAnything(this);
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public boolean isCanceled() {
        return cancelled;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"screenName\": \"%s\", \"inventory\": %s}", this.getEventName(), JsMacros.getScreenName((Screen) screen), inventory);
    }
    
}
