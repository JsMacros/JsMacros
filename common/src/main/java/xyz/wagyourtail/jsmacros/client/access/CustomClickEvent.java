package xyz.wagyourtail.jsmacros.client.access;


import net.minecraft.event.ClickEvent;

public class CustomClickEvent extends ClickEvent {
    Runnable event;
    
    public CustomClickEvent(Runnable event) {
        //TODO: switch to enum extension with mixin 9.0 or whenever Mumfrey gets around to it
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
