package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.jsmacros.core.Core;

public class BaseEvent {
    public final Core<?, ?> runner;
    protected boolean cancelled;

    public BaseEvent(Core<?, ?> runner) {
        this.runner = runner;
    }

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
        runner.profile.triggerEvent(this);
    }

}
