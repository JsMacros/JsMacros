package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventRecvMessage implements IEvent {
    public TextHelper text;
    
    public EventRecvMessage(Text message) {
        this.text = new TextHelper(message);
        
        profile.triggerMacroJoin(this);
    }
    
    public String toString() {
        return String.format("%s:{\"text\": %s}", this.getEventName(), text);
    }
}
