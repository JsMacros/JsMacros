package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;

public class BaseEvent {
    public static final BaseProfile profile = Core.getInstance().profile;
    protected boolean cancelled;

    public boolean cancellable() {
        return this.getClass().getAnnotation(Event.class).cancellable();
    }

    public boolean joinable() {
        return cancellable() || this.getClass().getAnnotation(Event.class).joinable();
    }

    /**
     * Cancel the event
     */
    public final void cancel() {
        if (cancellable()) {
            cancelled = true;
        } else {
            throw new UnsupportedOperationException("Event is not cancellable");
        }
    }

    public final boolean isCanceled() {
        return cancelled;
    }

    public String getEventName() {
        return this.getClass().getAnnotation(Event.class).value();
    }

    public void trigger() {
        profile.triggerEvent(this);
    }

}
