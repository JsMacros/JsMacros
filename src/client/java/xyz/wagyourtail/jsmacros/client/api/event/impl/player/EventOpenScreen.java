package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "OpenScreen", oldName = "OPEN_SCREEN")
public class EventOpenScreen extends BaseEvent {
    @Nullable
    public final IScreen screen;
    @DocletReplaceReturn("ScreenName")
    public final String screenName;

    public EventOpenScreen(Screen screen) {
        this.screen = (IScreen) screen;
        this.screenName = JsMacros.getScreenName(screen);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"screenName\": \"%s\"}", this.getEventName(), screenName);
    }

}
