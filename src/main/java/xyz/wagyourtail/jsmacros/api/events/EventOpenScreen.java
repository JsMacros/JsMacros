package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IScreen;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventOpenScreen implements IEvent {
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
