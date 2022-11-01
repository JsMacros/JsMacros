package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.ICancelable;

@Event("JoinedKey")
public class EventJoinedKey extends EventKey implements ICancelable {
    public boolean cancel;

    public EventJoinedKey(int action, int key, int mods) {
        super(action, key, mods);
    }

    @Override
    protected void trigger() {
        profile.triggerEventJoinNoAnything(this);
    }

    @Override
    public void cancel() {
        this.cancel = true;
    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }
    
}
