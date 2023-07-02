package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

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

    public void deleteById() {
        ((IChatHud) hud).jsmacros_removeMessageById(0);
    }

}
