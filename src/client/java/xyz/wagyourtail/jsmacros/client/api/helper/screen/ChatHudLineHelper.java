package xyz.wagyourtail.jsmacros.client.api.helper.screen;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

@SuppressWarnings("unused")
public class ChatHudLineHelper extends BaseHelper<ChatHudLine> {
    private final ChatHud hud;

    public ChatHudLineHelper(ChatHudLine base, ChatHud hud) {
        super(base);
        this.hud = hud;
    }

    public TextHelper getText() {
        return TextHelper.wrap(base.content());
    }

    public int getCreationTick() {
        return base.creationTick();
    }

    public ChatHudLineHelper delete() {
        hud.messages.removeIf((line) -> line == base);
        return this;
    }

    @Override
    public String toString() {
        return String.format("ChatHudLineHelper:{\"text\": \"%s\", \"creationTick\": %d}", base.content().getString(), base.creationTick());
    }

}
