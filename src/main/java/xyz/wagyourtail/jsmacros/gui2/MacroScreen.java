package xyz.wagyourtail.jsmacros.gui2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.containers.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.gui2.containers.EventChooser;
import xyz.wagyourtail.jsmacros.gui2.containers.FileChooser;
import xyz.wagyourtail.jsmacros.gui2.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.gui2.containers.MacroListTopbar;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.OverlayContainer;
import xyz.wagyourtail.jsmacros.gui2.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.runscript.RunScript;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class MacroScreen extends Screen {
    protected Screen parent;
    protected MacroListTopbar topbar;
    protected Scrollbar macroScroll;
    protected List<MacroContainer> macros = new ArrayList<>();
    protected int topScroll;
    protected Button keyScreen;
    protected Button eventScreen;
    protected Button profileScreen;
    protected OverlayContainer overlay;
    
    public MacroScreen(Screen parent) {
        super(new TranslatableText("jsmacros.title"));
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        macros.clear();
        overlay = null;
        client.keyboard.enableRepeatEvents(true);
        keyScreen = this.addButton(new Button(0, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.keys"), null));

        eventScreen = this.addButton(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.events"), null));

        profileScreen = this.addButton(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.profile"), null));

        topbar = new MacroListTopbar(this.width / 12, 25, this.width * 5 / 6, 14, this.textRenderer, MacroEnum.KEY_RISING, this::addButton, this::addMacro, this::runFile);

        topScroll = 40;
        macroScroll = this.addButton(new Scrollbar(this.width * 23 / 24 - 4, 50, 8, this.height - 75, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
    
        this.addButton(new Button(0, this.height - 12, this.width / 12, 12, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.running"), (btn) -> {
            client.openScreen(new CancelScreen(this));
        }));
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (overlay == null) {
            macroScroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        } else {
            if (overlay.scroll != null) overlay.scroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void addMacro(RawMacro macro) {
        macros.add(new MacroContainer(this.width / 12, topScroll + macros.size() * 16, this.width * 5 / 6, 14, this.textRenderer, macro, this::addButton, this::confirmRemoveMacro, this::setFile, this::setEvent));
        macroScroll.setScrollPages((macros.size() * 16) / (double) Math.max(1, this.height - 40));
    }

    public void setFile(MacroContainer macro) {
        File f = new File(jsMacros.config.macroFolder, macro.getRawMacro().scriptFile);
        File dir = jsMacros.config.macroFolder;
        if (!f.equals(jsMacros.config.macroFolder)) dir = f.getParentFile();
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, dir, f, this::addButton, this::removeButton, this::closeOverlay, this::setFocused, macro::setFile));
    }
    
    public void setEvent(MacroContainer macro) {
        openOverlay(new EventChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, macro.getRawMacro().eventkey, this::addButton, this::removeButton, this::closeOverlay, macro::setEventType));
    }
    
    public void runFile(MacroListTopbar m) {
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, jsMacros.config.macroFolder, null, this::addButton, this::removeButton, this::closeOverlay, this::setFocused, (file) -> {
            try {
                RunScript.exec(new RawMacro(MacroEnum.EVENT, "", file.getCanonicalPath().substring(jsMacros.config.macroFolder.getCanonicalPath().length()), true), "", null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
    
    public void openOverlay(OverlayContainer overlay) {
        for (AbstractButtonWidget b : buttons) {
            overlay.savedBtnStates.put(b, b.active);
            b.active = false;
        }
        this.overlay = overlay;
        overlay.init();
    }

    public void removeButton(AbstractButtonWidget btn) {
        buttons.remove(btn);
        children.remove(btn);
    }
    
    public void closeOverlay(OverlayContainer overlay) {
        if (overlay == null) return;
        for (AbstractButtonWidget b : overlay.getButtons()) {
            removeButton(b);
        }
        for (AbstractButtonWidget b : overlay.savedBtnStates.keySet()) {
            b.active = overlay.savedBtnStates.get(b);
        }
        if (this.overlay == overlay) this.overlay = null;
    }

    public void confirmRemoveMacro(MacroContainer macro) {
        openOverlay(new ConfirmOverlay(width / 2 - 100, height / 2 - 50, 200, 100, this.textRenderer, new TranslatableText("jsmacros.confirmdeletemacro"), this::addButton, this::removeButton, this::closeOverlay, (conf) -> {
            removeMacro(macro);
        }));
    }
    
    public void removeMacro(MacroContainer macro) {
        Profile.registry.removeRawMacro(macro.getRawMacro());
        for (AbstractButtonWidget b : macro.getButtons()) {
            removeButton(b);
        }
        macros.remove(macro);
        setMacroPos();
    }
    
    private void onScrollbar(double page) {
        topScroll = 40 - (int) (page * (height - 60));
        setMacroPos();
    }
    
    public void setMacroPos() {
        int i = 0;
        for (MacroContainer m : macros) {
            if (topScroll + i * 16 < 40) m.setVisible(false);
            else m.setVisible(true);
            m.setPos(this.width / 12, topScroll + (i++) * 16, this.width * 5 / 6, 14);
        }

        macroScroll.setScrollPages((macros.size() * 16) / (double) Math.max(1, this.height - 40));
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (overlay != null) {
                this.overlay.closeOverlay(this.overlay.getChildOverlay());
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (matricies == null) return;
        this.renderBackground(matricies, 0);
        
        topbar.render(matricies, mouseX, mouseY, delta);

        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            ((Button) b).render(matricies, mouseX, mouseY, delta);
        }

        for (MacroContainer macro : ImmutableList.copyOf(this.macros)) {
            macro.render(matricies, mouseX, mouseY, delta);
        }
        
        drawCenteredString(matricies, this.textRenderer, jsMacros.profile.profileName, this.width * 7 / 12, 5, 0x7F7F7F);

        fill(matricies, this.width * 5 / 6 - 1, 0, this.width * 5 / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 - 1, 0, this.width / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 * 2, 0, this.width / 6 * 2 + 2, 20, 0xFFFFFFFF);
        fill(matricies, 0, 20, width, 22, 0xFFFFFFFF);

        if (overlay != null) overlay.render(matricies, mouseX, mouseY, delta);
    }
    
    public void removed() {
        client.keyboard.enableRepeatEvents(false);
    }

    public boolean shouldCloseOnEsc() {
        return this.overlay == null;
    }

    public void onClose() {
        client.openScreen(parent);
        jsMacros.profile.saveProfile();
    }
}
