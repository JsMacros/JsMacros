package xyz.wagyourtail.jsmacros.gui2;

import java.util.ArrayList;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.MacroContainer;
import xyz.wagyourtail.jsmacros.gui2.elements.MacroListTopbar;
import xyz.wagyourtail.jsmacros.gui2.elements.OverlayContainer;
import xyz.wagyourtail.jsmacros.gui2.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class KeyMacrosScreen extends Screen {
    private Screen parent;
    private MacroListTopbar topbar;
    private Scrollbar macroScroll;
    private ArrayList<MacroContainer> macros = new ArrayList<>();
    private int topScroll;
    
    public KeyMacrosScreen(Screen parent) {
        super(new TranslatableText("jsmacros.title"));
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        macros.clear();
        client.keyboard.enableRepeatEvents(true);
        Button keys = this.addButton(new Button(0, 0, this.width / 6 - 1, 20, 0x4FFFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Keys"), null));
        keys.active = false;
        
        this.addButton(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Events"), (btn) -> {
            client.openScreen(new EventMacrosScreen(this));
        }));
        
        this.addButton(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Profile"), (btn) -> {
            client.openScreen(new ProfileScreen(this));
        }));
        
        topbar = new MacroListTopbar(this.width / 12, 25, this.width * 5 / 6, 14, this.textRenderer, MacroEnum.KEY_RISING, this::addButton, this::addMacro);
        
        topScroll = 40;
        macroScroll = this.addButton(new Scrollbar(this.width * 23 / 24 - 4, 50, 8, this.height - 75, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
       
        for (RawMacro macro : Profile.registry.getMacros().get("KEY").keySet()) {
            addMacro(macro);
        }
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        macroScroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    public void addMacro(RawMacro macro) {
        macros.add(new MacroContainer(this.width / 12, topScroll + macros.size() * 16, this.width * 5 / 6, 14, this.textRenderer, macro, this::addButton, this::removeMacro, this::openOverlay));
        macroScroll.setScrollPages((macros.size() * 16) / (double)Math.max(1, this.height - 40));
    }
    
    public void openOverlay(OverlayContainer overlay) {
        //for (AbstractButtonWidget b)
    }
    
    public void removeMacro(MacroContainer macro) {
        for (AbstractButtonWidget b : macro.getButtons()) {
            buttons.remove(b);
            children.remove(b);
        }
        macros.remove(macro);
        setMacroPos();
    }
    
    public void setMacroPos() {
        int i = 0;
        for (MacroContainer m : macros) {
            m.setPos(this.width / 12, topScroll + (i++) * 16, this.width * 5 / 6, 14);
        }
        
        macroScroll.setScrollPages((macros.size() * 16) / (double)Math.max(1, this.height - 40));
    }
    
    private void onScrollbar(double page) {
        topScroll = 40 - (int) (page * (height - 60));
        setMacroPos();
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        this.renderBackground(matricies, 0);
        
        topbar.render(matricies, mouseX, mouseY, delta);
        
        for (MacroContainer macro : macros) {
            macro.render(matricies, mouseX, mouseY, delta);
        }
        
        for(AbstractButtonWidget b : buttons) {
            ((Button)b).render(matricies, mouseX, mouseY, delta);
        }
        
        drawCenteredString(matricies, this.textRenderer, jsMacros.profile.profileName, this.width * 7 / 12, 5, 0x7F7F7F);
        
        fill(matricies, this.width * 5 / 6 - 1, 0, this.width * 5 / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 - 1, 0, this.width / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 * 2, 0, this.width / 6 * 2 + 2, 20, 0xFFFFFFFF);
        fill(matricies, 0, 20, width, 22, 0xFFFFFFFF);
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        } else {
            for (MacroContainer macro : macros) {
                if (!macro.keyPressed(keyCode, scanCode, modifiers)) return false;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (MacroContainer macro : macros) {
            if (!macro.mouseClicked(mouseX, mouseY, button)) return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    public void removed() {
        client.keyboard.enableRepeatEvents(false);
    }
    
    public void onClose() {
        client.openScreen(parent);
        jsMacros.profile.saveProfile();
    }
}
