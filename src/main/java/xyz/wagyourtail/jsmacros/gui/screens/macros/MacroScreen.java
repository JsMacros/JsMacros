package xyz.wagyourtail.jsmacros.gui.screens.macros;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IRawMacro;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui.BaseScreen;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.gui.elements.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.gui.elements.containers.MacroListTopbar;
import xyz.wagyourtail.jsmacros.gui.elements.overlays.AboutOverlay;
import xyz.wagyourtail.jsmacros.gui.elements.overlays.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.gui.elements.overlays.EventChooser;
import xyz.wagyourtail.jsmacros.gui.elements.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.gui.screens.CancelScreen;
import xyz.wagyourtail.jsmacros.gui.screens.editor.EditorScreen;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MacroScreen extends BaseScreen {
    protected MacroListTopbar topbar;
    protected Scrollbar macroScroll;
    protected List<MacroContainer> macros = new ArrayList<>();
    protected int topScroll;
    protected Button keyScreen;
    protected Button eventScreen;
    protected Button profileScreen;
    
    public MacroScreen(Screen parent) {
        super(new TranslatableText("jsmacros.title"), parent);
    }
    
    protected void init() {
        super.init();
        macros.clear();
        keyScreen = this.addButton(new Button(0, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.keys"), null));

        eventScreen = this.addButton(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.events"), null));

        profileScreen = this.addButton(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.profile"), null));

        topbar = new MacroListTopbar(this, this.width / 12, 25, this.width * 5 / 6, 14, this.textRenderer, IRawMacro.MacroType.KEY_RISING, this::addButton, this::addMacro, this::runFile);

        topScroll = 40;
        macroScroll = this.addButton(new Scrollbar(this.width * 23 / 24 - 4, 50, 8, this.height - 75, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
    
        this.addButton(new Button(0, this.height - 12, this.width / 12, 12, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.running"), (btn) -> {
            client.openScreen(new CancelScreen(this));
        }));
        
        this.addButton(new Button(this.width * 11 / 12, this.height - 12, this.width / 12, 12, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.about"), (btn) -> {
            this.openOverlay(new AboutOverlay(this.width / 4, this.height / 4, this.width / 2, this.height / 2, textRenderer, this::addButton, this::removeButton, this::closeOverlay));
        }));
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (overlay == null) {
            macroScroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void addMacro(RawMacro macro) {
        macros.add(new MacroContainer(this.width / 12, topScroll + macros.size() * 16, this.width * 5 / 6, 14, this.textRenderer, macro, this::addButton, this::confirmRemoveMacro, this::setFile, this::setEvent, this::editFile));
        macroScroll.setScrollPages(((macros.size() + 1) * 16) / (double) Math.max(1, this.height - 40));
    }

    public void setFile(MacroContainer macro) {
        File f = new File(JsMacros.config.macroFolder, macro.getRawMacro().scriptFile);
        File dir = JsMacros.config.macroFolder;
        if (!f.equals(JsMacros.config.macroFolder)) dir = f.getParentFile();
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, dir, f, this::addButton, this::removeButton, this::closeOverlay, this::setFocused, macro::setFile, this::editFile));
    }
    
    public void setEvent(MacroContainer macro) {
        openOverlay(new EventChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, macro.getRawMacro().eventkey, this::addButton, this::removeButton, this::closeOverlay, macro::setEventType));
    }
    
    public void runFile() {
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, JsMacros.config.macroFolder, null, this::addButton, this::removeButton, this::closeOverlay, this::setFocused, (file) -> {
            try {
                RunScript.exec(new RawMacro(IRawMacro.MacroType.EVENT, "", file.getCanonicalPath().substring(JsMacros.config.macroFolder.getCanonicalPath().length()), true), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, this::editFile));
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
        topScroll = 40 - (int) (page * (height - 40));
        setMacroPos();
    }
    
    public void setMacroPos() {
        int i = 0;
        for (MacroContainer m : macros) {
            if (topScroll + i * 16 < 40) m.setVisible(false);
            else m.setVisible(true);
            m.setPos(this.width / 12, topScroll + (i++) * 16, this.width * 5 / 6, 14);
        }
    }

    public void editFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            client.openScreen(new EditorScreen(this, file));
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (matrices == null) return;
        this.renderBackground(matrices, 0);
        
        topbar.render(matrices, mouseX, mouseY, delta);

        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            b.render(matrices, mouseX, mouseY, delta);
        }

        for (MacroContainer macro : ImmutableList.copyOf(this.macros)) {
            macro.render(matrices, mouseX, mouseY, delta);
        }
        
        drawCenteredString(matrices, this.textRenderer, JsMacros.profile.profileName, this.width * 7 / 12, 5, 0x7F7F7F);

        fill(matrices, this.width * 5 / 6 - 1, 0, this.width * 5 / 6 + 1, 20, 0xFFFFFFFF);
        fill(matrices, this.width / 6 - 1, 0, this.width / 6 + 1, 20, 0xFFFFFFFF);
        fill(matrices, this.width / 6 * 2, 0, this.width / 6 * 2 + 2, 20, 0xFFFFFFFF);
        fill(matrices, 0, 20, width, 22, 0xFFFFFFFF);

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void onClose() {
        JsMacros.profile.saveProfile();
        super.onClose();
    }
}
