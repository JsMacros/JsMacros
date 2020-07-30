package xyz.wagyourtail.jsmacros.gui2;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.containers.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.gui2.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;

public class KeyMacrosScreen extends MacroScreen {
    
    public KeyMacrosScreen(Screen parent) {
        super(parent);
    }

    public void init() {
        super.init();
        keyScreen.setColor(0x4FFFFFFF);
        
        eventScreen.onPress = (btn) -> {
            client.openScreen(new EventMacrosScreen(this));
        };

        profileScreen.onPress = (btn) -> {
            client.openScreen(new ProfileScreen(this));
        };
        
        List<RawMacro> macros = new ArrayList<>(Profile.registry.getMacros().get("KEY").keySet()); 
        
        Collections.sort(macros, new RawMacro.sortRawMacro());
        
        if (Profile.registry.getMacros().containsKey("KEY"))
            for (RawMacro macro : macros) {
                if (macro.type != MacroEnum.EVENT) addMacro(macro);
            }
        
        if (jsMacros.jythonFailed) {
            this.openOverlay(new ConfirmOverlay(width / 2 - 100, height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.jythonfail"), this::addButton, this::removeButton, this::closeOverlay, (conf) -> {
                jsMacros.jythonFailed = false;
            }));
        }
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        String translationKey = jsMacros.getKeyModifiers(modifiers);
        if (!translationKey.equals("")) translationKey += "+";
        translationKey += InputUtil.fromKeyCode(keyCode, scanCode).getTranslationKey();
        for (MacroContainer macro : macros) {
            if (!macro.onKey(translationKey)) return false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        int mods = 0;
        if (hasShiftDown()) mods += 1;
        if (hasControlDown()) mods += 2;
        if (hasAltDown()) mods += 4;
        String translationKey = jsMacros.getKeyModifiers(mods);
        if (!translationKey.equals("")) translationKey += "+";
        translationKey += InputUtil.Type.MOUSE.createFromCode(button).getTranslationKey();
        for (MacroContainer macro : macros) {
            if (!macro.onKey(translationKey)) return false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
