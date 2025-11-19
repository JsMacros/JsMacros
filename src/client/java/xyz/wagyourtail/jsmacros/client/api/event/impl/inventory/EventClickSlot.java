package xyz.wagyourtail.jsmacros.client.api.event.impl.inventory;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.Inventory;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * event triggered when the user "clicks" a slot in an inventory
 *
 * @author Wagyourtail
 * @since 1.6.4
 */
@Event(value = "ClickSlot", cancellable = true)
public class EventClickSlot extends BaseEvent {
    protected final HandledScreen<?> screen;
    /**
     * <a href="https://wiki.vg/Protocol#Click_Window" target="_blank">https://wiki.vg/Protocol#Click_Window</a>
     */
    public final int mode;
    @DocletReplaceReturn("ClickSlotButton")
    public final int button;
    public final int slot;

    public EventClickSlot(HandledScreen<?> screen, int mode, int button, int slot) {
        super(JsMacrosClient.clientCore);
        this.screen = screen;
        this.mode = mode;
        this.button = button;
        this.slot = slot;
    }

    /**
     * @return inventory associated with the event
     */
    public Inventory<?> getInventory() {
        return Inventory.create(screen);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"slot\": %d, \"screen\": \"%s\"}", this.getEventName(), slot, JsMacrosClient.getScreenName(screen));
    }

}
