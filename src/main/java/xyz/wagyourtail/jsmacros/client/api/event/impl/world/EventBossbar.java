package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import net.minecraft.client.gui.hud.ClientBossBar;
import xyz.wagyourtail.doclet.DocletEnumType;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.BossBarHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.UUID;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "Bossbar", oldName = "BOSSBAR_UPDATE")
public class EventBossbar extends BaseEvent {
    public final BossBarHelper bossBar;
    public final String uuid;
    @DocletReplaceReturn("BossBarUpdateType")
    @DocletEnumType(name = "BossBarUpdateType", type =
            """
            'ADD' | 'REMOVE' | 'UPDATE_PERCENT'
            | 'UPDATE_NAME' | 'UPDATE_STYLE' | 'UPDATE_PROPERTIES'
            """
    )
    public final String type;

    public EventBossbar(String type, UUID uuid, ClientBossBar bossBar) {
        if (bossBar != null) {
            this.bossBar = new BossBarHelper(bossBar);
        } else {
            this.bossBar = null;
        }
        this.uuid = uuid.toString();
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"bossBar\": %s}", this.getEventName(), bossBar != null ? bossBar.toString() : uuid);
    }

}
