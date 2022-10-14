package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;


public class ChatHudLineHelper extends BaseHelper<ChatLine> {
    private GuiNewChat hud;

    public ChatHudLineHelper(ChatLine base, GuiNewChat hud) {
        super(base);
        this.hud = hud;
    }

    public TextHelper getText() {
        return new TextHelper(base.getText());
    }

    public int getId() {
        return base.getId();
    }

    public int getCreationTick() {
        return base.getCreationTick();
    }

    public void deleteById() {
        ((IChatHud) hud).jsmacros_removeMessageById(base.getId());
    }

}
