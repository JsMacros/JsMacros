package xyz.wagyourtail.jsmacros.gui;

import java.io.File;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.events.EventTypesEnum;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;

public class MacroEditScreen extends Screen {
    private Screen parent;
    private MacroTypeWidget macroTypeWidget;
    private ButtonWidget buttonSetKey;
    private RawMacro macro;
    private RawMacro newMacro;
    private boolean selectKey = false;
    private TextFieldWidget fileBox;

    protected MacroEditScreen(Screen parent, RawMacro macro) {
        super(new TranslatableText("jsmacros.editmacro"));
        this.parent = parent;
        this.macro = macro;
        this.newMacro = new RawMacro(macro.type, macro.eventkey, macro.scriptFile);
    }

    protected void init() {
        super.init();
        minecraft.keyboard.enableRepeatEvents(true);
        macroTypeWidget = new MacroTypeWidget(this.minecraft, this, 200, this.height);
        macroTypeWidget.setLeftPos(this.width / 2 - 4 - 200);
        this.children.add(this.macroTypeWidget);

        //buttonSetKey
        this.buttonSetKey = (ButtonWidget) this.addButton(new ButtonWidget(this.width / 2 + 100 - 50, this.height / 4, 100, 20, macro.type != MacroEnum.EVENT ? macro.eventkey : "", (buttonWidget) -> {
            MacroTypeWidget.MacroTypeEntry entry = (MacroTypeWidget.MacroTypeEntry) this.macroTypeWidget.getSelected();
            if (entry != null) {
                this.buttonSetKey.setMessage("");
                selectKey = true;
            }
        }));
        
        //fileBox
        this.fileBox = new TextFieldWidget(this.font, this.width / 2 + 100 - 100, this.height / 4 + 50 + 5, 200, 20, this.fileBox, I18n.translate("jsmacros.filepath"));
        this.fileBox.setChangedListener((string) -> {
            File path = new File(jsMacros.config.macroFolder, string);
            if (path.isFile()) {
                this.fileBox.setEditableColor(0x00FF00);
                newMacro.scriptFile = path;
            } else {
                this.fileBox.setEditableColor(0xFF0000);
            }
        });
        this.children.add(this.fileBox);
        this.fileBox.setText(macro.scriptFileName());
        
        // save
        this.addButton(new ButtonWidget(this.width / 2 + 100 - 100, this.height / 4 + 100 + 5, 200, 20, I18n.translate("jsmacros.save"), (buttonWidget) -> {
            if (!newMacro.equals(macro)) {
                jsMacros.profile.removeMacro(macro);
                jsMacros.profile.addMacro(newMacro);
            }
            minecraft.openScreen(parent);
        }));

        // cancel
        this.addButton(new ButtonWidget(this.width / 2 + 100 - 100, this.height / 4 + 120 + 10, 200, 20, I18n.translate("gui.cancel"), (buttonWidget) -> {
            minecraft.openScreen(parent);
        }));
        
        if (this.newMacro.type != null) this.select(this.newMacro.type != MacroEnum.EVENT ? this.newMacro.type.toString() : this.macro.eventkey);
        else this.select(MacroEnum.KEY_RISING.toString());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectKey) {
            InputUtil.KeyCode key = InputUtil.Type.KEYSYM.createFromCode(keyCode);
            newMacro.eventkey = key.toString();
            selectKey = false;
            this.buttonSetKey.setMessage(jsMacros.getLocalizedName(key));
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void updateButtonStates() {
        MacroTypeWidget.MacroTypeEntry entry = (MacroTypeWidget.MacroTypeEntry) this.macroTypeWidget.getSelected();
        if (entry != null) for (MacroEnum e : MacroEnum.values()) {
            if (entry.macroType == e.toString()) {
                this.buttonSetKey.setMessage(newMacro.type != MacroEnum.EVENT ? newMacro.eventkey != null && newMacro.eventkey != "" ? jsMacros.getLocalizedName(InputUtil.fromName(newMacro.eventkey)) : "" : "");
                this.buttonSetKey.active = true;
                return;
            }
        }
        this.buttonSetKey.setMessage("");
        this.buttonSetKey.active = false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectKey) {
            InputUtil.KeyCode key = InputUtil.Type.MOUSE.createFromCode(button);
            newMacro.eventkey = key.toString();
            selectKey = false;
            this.buttonSetKey.setMessage(jsMacros.getLocalizedName(key));
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void render(int mouseX, int mouseY, float delta) {
        this.renderDirtBackground(0);
        this.macroTypeWidget.render(mouseX, mouseY, delta);
        this.fileBox.render(mouseX, mouseY, delta);
        Text setKey = new TranslatableText("jsmacros.setkey");
        this.minecraft.textRenderer.draw(setKey.asFormattedString(), this.width / 2 + 100 - this.minecraft.textRenderer.getStringWidth(setKey.asFormattedString()) / 2, this.height / 4 - 15, 0xFFFFFF);
        Text setFile = new TranslatableText("jsmacros.setfile");
        this.minecraft.textRenderer.draw(setFile.asFormattedString(), this.width / 2 + 100 - this.minecraft.textRenderer.getStringWidth(setFile.asFormattedString()) / 2, this.height / 4 + 40, 0xFFFFFF);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

    public void select(MacroTypeWidget.MacroTypeEntry entry) {
        boolean flag = true;
        for (MacroEnum e : MacroEnum.values()) {
            if (e.toString() == entry.macroType) {
                if (this.newMacro.type == MacroEnum.EVENT) this.newMacro.eventkey = "";
                this.newMacro.type = e;
                flag = false;
                break;
            }
        }
        if (flag) {
            for (EventTypesEnum e : EventTypesEnum.values()) {
                if (e.toString() == entry.macroType) {
                    this.newMacro.eventkey = e.toString();
                    break;
                }
            }
            this.newMacro.type = MacroEnum.EVENT;
        }
        
        this.macroTypeWidget.setSelected(entry);
        this.updateButtonStates();
    }
    
    public void select(String type) {
        for (MacroTypeWidget.MacroTypeEntry e : this.macroTypeWidget.macroTypes) {
            if (e.macroType.equals(type)) {
                this.select(e);
            }
        }
    }

    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
}