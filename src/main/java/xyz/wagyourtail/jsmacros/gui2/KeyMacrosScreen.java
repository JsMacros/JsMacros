package xyz.wagyourtail.jsmacros.gui2;

import com.mojang.blaze3d.systems.RenderSystem;

import xyz.wagyourtail.jsmacros.gui2.keymacros.Button;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.TranslatableText;

public class KeyMacrosScreen extends Screen {
    private Screen parent;
    private Button btn;
    
    public KeyMacrosScreen(Screen parent) {
        super(new TranslatableText("jsmacros.title"));
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        btn = (Button) this.addButton(new Button(this.width / 2, this.height / 2, 32, 32, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, "er"));
    }
    
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground(0);
        RenderSystem.enableBlend();
        for(AbstractButtonWidget b : buttons) {
            ((Button)b).render(mouseX, mouseY, delta);
         }
        RenderSystem.disableBlend();
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
         return false;
        
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) return true;
        return false;
    }
    
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) return true;
        return false;
        
    }
}
