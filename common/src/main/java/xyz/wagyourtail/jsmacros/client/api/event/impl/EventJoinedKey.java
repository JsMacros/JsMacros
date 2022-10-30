package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.Event;

@Event("JoinedKey")
public class EventJoinedKey extends EventKey {
    public boolean cancel;

    public EventJoinedKey(int action, int key, int mods) {
        super(action, key, mods);
    }

    @Override
    protected void trigger() {
        profile.triggerEventJoinNoAnything(this);
    }

}
