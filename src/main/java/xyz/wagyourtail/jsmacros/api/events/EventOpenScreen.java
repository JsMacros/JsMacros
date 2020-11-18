package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "OpenScreen", oldName = "OPEN_SCREEN")
public class EventOpenScreen implements BaseEvent {
    public final IScreen screen;
    public final String screenName;
    
    public EventOpenScreen(Screen screen) {
        this.screen = (IScreen) screen;
        this.screenName = JsMacros.getScreenName(screen);
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"screenName\": \"%s\"}", this.getEventName(), screenName);
    }
    
}
