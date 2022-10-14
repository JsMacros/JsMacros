package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * event triggered when an item is dropped
 *
 * @author Wagyourtail
 * @since 1.6.4
 */
@Event("DropSlot")
public class EventDropSlot implements BaseEvent {
    protected static final Minecraft mc = Minecraft.getInstance();

    protected final GuiContainer screen;
    public final int slot;
    /**
     *  whether it's all or a single item being dropped
     */
    public final boolean all;
    /**
     * set to {@code true} to cancel the default action
     */
    public boolean cancel = false;

    public EventDropSlot(GuiContainer screen, int slot, boolean all) {
        this.screen = screen;
        this.slot = slot;
        this.all = all;

        profile.triggerEventJoinNoAnything(this);
    }


    /**
     * @return inventory associated with the event
     */
    public Inventory<?> getInventory() {
        if (screen == null) {
            assert mc.player != null;
            return Inventory.create(new GuiInventory(mc.player));
        }
        return Inventory.create(screen);
    }


    public String toString() {
        return String.format("%s:{\"slot\": %d, \"screen\": \"%s\"}", this.getEventName(), slot, JsMacros.getScreenName(screen));
    }

}
