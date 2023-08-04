package xyz.wagyourtail.jsmacros.client.api.event.impl.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.Inventory;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * event triggered when an item is dropped
 *
 * @author Wagyourtail
 * @since 1.6.4
 */
@Event(value = "DropSlot", cancellable = true)
public class EventDropSlot extends BaseEvent {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    protected final HandledScreen<?> screen;
    public final int slot;
    /**
     * whether it's all or a single item being dropped
     */
    public final boolean all;
    /**
     * set to {@code true} to cancel the default action
     */
    public boolean cancel = false;

    public EventDropSlot(HandledScreen<?> screen, int slot, boolean all) {
        this.screen = screen;
        this.slot = slot;
        this.all = all;
    }

    /**
     * @return inventory associated with the event
     */
    public Inventory<?> getInventory() {
        if (screen == null) {
            assert mc.player != null;
            return Inventory.create(new InventoryScreen(mc.player));
        }
        return Inventory.create(screen);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"slot\": %d, \"screen\": \"%s\"}", this.getEventName(), slot, JsMacros.getScreenName(screen));
    }

}
