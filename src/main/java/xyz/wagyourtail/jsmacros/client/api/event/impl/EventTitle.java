package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletEnumType;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "Title", oldName = "TITLE", cancellable = true)
public class EventTitle extends BaseEvent {
    @DocletReplaceReturn("TitleType")
    @DocletEnumType(name = "TitleType", type = "'TITLE' | 'SUBTITLE' | 'ACTIONBAR'")
    public final String type;
    @Nullable
    public TextHelper message;

    public EventTitle(String type, Text message) {
        this.type = type;
        this.message = TextHelper.wrap(message);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"type\": \"%s\", \"message\": \"%s\"}", this.getEventName(), type, message);
    }

}
