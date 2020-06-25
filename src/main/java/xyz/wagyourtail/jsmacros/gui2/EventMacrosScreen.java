package xyz.wagyourtail.jsmacros.gui2;

import java.util.ArrayList;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.MacroContainer;
import xyz.wagyourtail.jsmacros.gui2.elements.MacroListTopbar;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class EventMacrosScreen extends Screen {
    private Screen parent;
    private MacroListTopbar topbar;
    private ArrayList<MacroContainer> macros = new ArrayList<>();
    private int topScroll = 40;
    
    public EventMacrosScreen(Screen parent) {
        super(new TranslatableText("jsmacros.title"));
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        macros.clear();
        client.keyboard.enableRepeatEvents(true);
        this.addButton(new Button(0, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Keys"), (btn) -> {
            client.openScreen(parent);
        }));
        
        Button events = this.addButton(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, 0x4FFFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Events"), null));
        events.active = false;
        
        this.addButton(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Profile"), (btn) -> {
            client.openScreen(new ProfileScreen(this.parent));
        }));

        topbar = new MacroListTopbar(this.width / 12, 25, this.width * 5 / 6, 14, this.textRenderer, MacroEnum.EVENT, this::addButton, this::addMacro);
        
        for (String event : Profile.registry.events) {
            if (event != "KEY" && Profile.registry.getMacros().containsKey(event))
                for (RawMacro macro : Profile.registry.getMacros().get(event).keySet()) {
                    addMacro(macro);
                }
        }
        if (Profile.registry.getMacros().containsKey(""))
            for (RawMacro macro : Profile.registry.getMacros().get("").keySet()) {
                addMacro(macro);
            }
    }
    
    public void addMacro(RawMacro macro) {
        macros.add(new MacroContainer(this.width / 12, topScroll + macros.size() * 16, this.width * 5 / 6, 14, this.textRenderer, macro, this::addButton, this::removeMacro));
    }
    
    public void removeMacro(MacroContainer macro) {
        for (AbstractButtonWidget b : macro.getButtons()) {
            buttons.remove(b);
        }
        macros.remove(macro);
        Profile.registry.removeMacro(macro.getRawMacro());
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
    }
}
