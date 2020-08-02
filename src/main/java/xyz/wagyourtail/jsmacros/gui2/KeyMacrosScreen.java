package xyz.wagyourtail.jsmacros.gui2;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.macros.IEventListener;
import xyz.wagyourtail.jsmacros.profile.Profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;

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
        
        List<IEventListener> listeners = Profile.registry.getListeners().get("KEY");
        List<RawMacro> macros = new ArrayList<>();
        
        for (IEventListener event : listeners) {
            if (event instanceof BaseMacro) macros.add(((BaseMacro) event).getRawMacro());
        }
        
        Collections.sort(macros, new RawMacro.sortRawMacro());
        
        for (RawMacro macro : macros) {
            addMacro(macro);
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
