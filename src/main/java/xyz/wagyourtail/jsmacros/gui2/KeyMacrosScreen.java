package xyz.wagyourtail.jsmacros.gui2;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.MacroListTopbar;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class KeyMacrosScreen extends Screen {
    private Screen parent;
    private MacroListTopbar topbar;
    
    public KeyMacrosScreen(Screen parent) {
        super(new TranslatableText("jsmacros.title"));
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        client.keyboard.enableRepeatEvents(true);
        Button keys = this.addButton(new Button(0, 0, this.width / 6 - 1, 20, 0x4FFFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Keys"), null));
        keys.active = false;
        
        this.addButton(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Events"), (btn) -> {
            client.openScreen(new EventMacrosScreen(this));
        }));
        
        this.addButton(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Profile"), (btn) -> {
            client.openScreen(new ProfileScreen(this));
        }));
        
        topbar = new MacroListTopbar(this.width / 12, 25, this.width * 5 / 6, 12, this.textRenderer);
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        this.renderBackground(matricies, 0);
        
        topbar.render(matricies, mouseX, mouseY, delta);
        
        for(AbstractButtonWidget b : buttons) {
            ((Button)b).render(matricies, mouseX, mouseY, delta);
        }
        
        drawCenteredString(matricies, this.textRenderer, jsMacros.profile.profileName, this.width * 7 / 12, 5, 0x7F7F7F);
        
        fill(matricies, this.width * 5 / 6 - 1, 0, this.width * 5 / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 - 1, 0, this.width / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 * 2, 0, this.width / 6 * 2 + 2, 20, 0xFFFFFFFF);
        fill(matricies, 0, 20, width, 22, 0xFFFFFFFF);
        
    }
    
    public void removed() {
        client.keyboard.enableRepeatEvents(false);
    }
    
    public void onClose() {
        client.openScreen(parent);
    }
}
