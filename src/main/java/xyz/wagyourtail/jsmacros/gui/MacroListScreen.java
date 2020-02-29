package xyz.wagyourtail.jsmacros.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

public class MacroListScreen extends Screen {
    private Screen parent;
    private MacroListWidget macroListWidget;
    private ButtonWidget buttonEdit;
    private ButtonWidget buttonRun;
    private ButtonWidget buttonDelete;
    private ButtonWidget buttonEditFile;
    
    public MacroListScreen(Screen parent) {
        super(new TranslatableText("jsmacros.title"));
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        minecraft.keyboard.enableRepeatEvents(true);
        macroListWidget = new MacroListWidget(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
        this.children.add(this.macroListWidget);
        
        //buttonEdit
        this.buttonEdit = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20, I18n.translate("jsmacros.edit"), (buttonWidget) -> {
            MacroListWidget.MacroEntry entry = (MacroListWidget.MacroEntry)this.macroListWidget.getSelected();
            if (entry != null)
                this.edit(entry.getRawMacro());
        }));
        
        //buttonEditFile
        this.buttonEditFile = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20, I18n.translate("jsmacros.editfile"), (buttonWidget) -> {
            MacroListWidget.MacroEntry entry = (MacroListWidget.MacroEntry)this.macroListWidget.getSelected();
            if (entry != null) {
                System.out.println("broken...");
                //also does not work
                //Util.getOperatingSystem().open(entry.getRawMacro().scriptFile);
                //does not work...
                //Runtime.getRuntime().exec("cmd.exe \\c \""+entry.getRawMacro().scriptFile.toString()+"\"");
            }
        }));
        
        //buttonRun
        this.buttonRun = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 28, 70, 20, I18n.translate("jsmacros.run"), (buttonWidget) -> {
            MacroListWidget.MacroEntry entry = (MacroListWidget.MacroEntry)this.macroListWidget.getSelected();
            if (entry != null) RunScript.exec(entry.getRawMacro(), null);
        }));
        
        //buttonDelete
        this.buttonDelete = (ButtonWidget)this.addButton(new ButtonWidget(this.width / 2 - 74, this.height - 28, 70, 20, I18n.translate("jsmacros.delete"), (buttonWidget) -> {
            MacroListWidget.MacroEntry entry = (MacroListWidget.MacroEntry)this.macroListWidget.getSelected();
            if (entry != null) {
                String s = entry.getRawMacro().eventkey;
                Text text = new TranslatableText("jsmacros.deleteQuestion", new Object[0]);
                Text text2 = new TranslatableText("jsmacros.deleteWarning", new Object[]{s});
                String string2 = I18n.translate("jsmacros.deleteButton");
                String string3 = I18n.translate("gui.cancel");
                this.minecraft.openScreen(new ConfirmScreen(this::removeEntry, text, text2, string2, string3));
            }
        }));
        
        //buttonCancel
        this.addButton(new ButtonWidget(this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.translate("jsmacros.close"), (buttonWidget) -> {
            this.minecraft.openScreen(this.parent);
        }));
        
        //buttonOpenFolder
        this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 28, 70, 20, I18n.translate("jsmacros.openfolder"), (buttonWidget) -> {
            Util.getOperatingSystem().open(jsMacros.config.macroFolder);
            //Runtime.getRuntime().exec("explorer.exe \"" + jsMacros.config.macroFolder.toString()+"\"");
        }));
        
        //buttonNew
        this.addButton(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.translate("jsmacros.add"), (buttonWidget) -> {
            edit(new RawMacro(null, null, null));
        }));
        
        this.addButton(new ButtonWidget(55, 10, 100, 20, I18n.translate("jsmacros.stoprunning"), (buttonWidget) -> {
            this.minecraft.openScreen(new MacroCancelScreen(this));
        }));
        
        this.updateButtonActivationStates();
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private void removeEntry(boolean confirmAction) {
        MacroListWidget.MacroEntry entry = (MacroListWidget.MacroEntry)this.macroListWidget.getSelected();
        if (confirmAction && entry != null) {
            jsMacros.profile.removeMacro(entry.getRawMacro());
        }
        this.minecraft.openScreen(this);
    }
    
    private void updateButtonActivationStates() {
        this.buttonDelete.active = false;
        this.buttonEdit.active = false;
        this.buttonEditFile.active = false;
        this.buttonRun.active = false;
        MacroListWidget.MacroEntry entry = (MacroListWidget.MacroEntry)this.macroListWidget.getSelected();
        if (entry != null) {
            this.buttonDelete.active = true;
            this.buttonEdit.active = true;
            //this.buttonEditFile.active = true;  //broken so disabled...
            this.buttonRun.active = true;
        }
    }

    private void edit(RawMacro macro) {
        this.minecraft.openScreen(new MacroEditScreen(this, macro));
    }
    
    public ArrayList<RawMacro> getMacroList() {
        return jsMacros.profile.toRawProfile();
    }
    
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.macroListWidget.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

    public void select(MacroListWidget.MacroEntry entry) {
        this.macroListWidget.setSelected(entry);
        this.updateButtonActivationStates();
    }
    
    public void removed() {
        jsMacros.profile.saveProfile();
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
}