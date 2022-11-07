package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

@SuppressWarnings("unused")
public class ChatHudLineHelper extends BaseHelper<ChatHudLine> {
    private ChatHud hud;

    public ChatHudLineHelper(ChatHudLine base, ChatHud hud) {
        super(base);
        this.hud = hud;
    }

    public TextHelper getText() {
        return new TextHelper(base.content());
    }

//    public int getId() {
//        return base.getId();
//    }

    public int getCreationTick() {
        return base.creationTick();
    }

    public ChatHudLineHelper deleteById() {
        ((IChatHud) hud).jsmacros_removeMessageById(0);
        return this;
    }

    @Override
    public String toString() {
        return String.format("ChatHudLineHelper:{\"text\": \"%s\", \"creationTick\": %d}", base.content().getString(), base.creationTick());
    }
    
}