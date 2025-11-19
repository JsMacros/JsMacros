package xyz.wagyourtail.jsmacros.access;

import net.minecraft.text.ClickEvent;

public class CustomClickEvent implements ClickEvent {
    Runnable event;

    public CustomClickEvent(Runnable event) {
        this.event = event;
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    public Runnable getEvent() {
        return event;
    }

    @Override
    public Action getAction() {
        return null;
    }
}
