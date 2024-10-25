package xyz.wagyourtail.jsmacros.access;

import net.minecraft.text.ClickEvent;

public class CustomClickEvent extends ClickEvent {
    Runnable event;

    public CustomClickEvent(Runnable event) {
        //TODO: switch to enum extension with mixin 9.0 or whenever Mumfrey gets around to it
        // https://github.com/SpongePowered/Mixin/issues/387
        super(null, null);
        this.event = event;
    }

    @Override
    public int hashCode() {
        return event.hashCode();
    }

    public Runnable getEvent() {
        return event;
    }

}
