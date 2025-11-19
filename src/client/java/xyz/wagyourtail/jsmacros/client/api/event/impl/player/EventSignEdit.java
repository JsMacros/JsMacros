package xyz.wagyourtail.jsmacros.client.api.event.impl.player;

import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.List;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "SignEdit", oldName = "SIGN_EDIT", cancellable = true)
public class EventSignEdit extends BaseEvent {
    public final Pos3D pos;
    public boolean closeScreen = false;
    public boolean front;
    @Nullable
    public List<String> signText;

    @SuppressWarnings("NullableProblems")
    public EventSignEdit(List<String> signText, int x, int y, int z, boolean front) {
        super(JsMacrosClient.clientCore);
        this.pos = new Pos3D(x, y, z);
        this.front = front;
        this.signText = signText;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"pos\": [%s]}", this.getEventName(), pos);
    }

}
