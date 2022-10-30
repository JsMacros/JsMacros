package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FKeyBind;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.wagyourgui.BaseScreen;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Key", oldName = "KEY")
public class EventKey implements BaseEvent {
    static final MinecraftClient mc = MinecraftClient.getInstance();
    public final int action;
    public final int key;
    public final int mods;

    private static final Set<Integer> wasNullOnDown = new HashSet<>();

    public EventKey(int action, int key, int mods) {
        this.action = action;
        this.key = key;
        this.mods = mods;

        trigger();
    }

    public static boolean parse(int key, int scancode, int action, int mods) {

        if (action == 1) FKeyBind.KeyTracker.press(key);
        else FKeyBind.KeyTracker.unpress(key);

        if (mc.currentScreen != null) {
            if (action != 0 || !wasNullOnDown.contains(key)) {
                if (Core.getInstance().config.getOptions(ClientConfigV2.class).disableKeyWhenScreenOpen) return false;
                if (mc.currentScreen instanceof BaseScreen) return false;
            }
        } else if (action == 1) {
            wasNullOnDown.add(key);
        }

        if (action == 0) {
            wasNullOnDown.remove(key);
        }

        // fix mods if it was a mod key
        if (action == 1) {
            if (key == 340 || key == 344) mods -= 1;
            else if (key == 341 || key == 345) mods -= 2;
            else if (key == 342 || key == 346) mods -= 4;
        }

        new EventKey(action, key, mods);
        EventJoinedKey ev = new EventJoinedKey(action, key, mods);
        return ev.cancel;
    }

    protected void trigger() {
        profile.triggerEvent(this);
    }

    public String toString() {
        return String.format("%s:{\"key\": \"%s\"}", this.getEventName(), key);
    }
}
