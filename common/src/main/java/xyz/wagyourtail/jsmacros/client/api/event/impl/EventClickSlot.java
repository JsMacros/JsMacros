package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * event triggered when the user "clicks" a slot in an inventory
 *
 * @author Wagyourtail
 * @since 1.6.4
 */
@Event("ClickSlot")
public class EventClickSlot implements BaseEvent {
    protected final HandledScreen screen;
    /**
     * <a href="https://wiki.vg/Protocol#Click_Window" target="_blank">https://wiki.vg/Protocol#Click_Window</a>
     */
    public final int mode;
    public final int button;
    public final int slot;
    /**
     * set to {@code true} to prevent the default action
     */
    public boolean cancel = false;

    public EventClickSlot(HandledScreen screen, int mode, int button, int slot) {
        this.screen = screen;
        this.mode = mode;
        this.button = button;
        this.slot = slot;
        profile.triggerEventJoinNoAnything(this);
    }

    /**
     * @return inventory associated with the event
     */
    public Inventory<?> getInventory() {
        return Inventory.create(screen);
    }

    public String toString() {
        return String.format("%s:{\"slot\": %d, \"screen\": \"%s\"}", this.getEventName(), slot, JsMacros.getScreenName(screen));
    }

}
