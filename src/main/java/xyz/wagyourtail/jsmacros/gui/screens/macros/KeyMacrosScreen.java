package xyz.wagyourtail.jsmacros.gui.screens.macros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.api.events.EventKey;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEventListener;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IRawMacro;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui.screens.ProfileScreen;
import xyz.wagyourtail.jsmacros.gui.screens.macros.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.profile.Profile;

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

        Set<IEventListener> listeners = Profile.registry.getListeners().get(EventKey.class.getSimpleName());
        List<RawMacro> macros = new ArrayList<>();

        if (listeners != null) for (IEventListener event : ImmutableList.copyOf(listeners)) {
            if (event instanceof BaseMacro && ((BaseMacro) event).getRawMacro().type != IRawMacro.MacroType.EVENT) macros.add(((BaseMacro) event).getRawMacro());
        }

        Collections.sort(macros, JsMacros.config.getSortComparator());

        for (RawMacro macro : macros) {
            addMacro(macro);
        }
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        String translationKey = EventKey.getKeyModifiers(modifiers);
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
        String translationKey = EventKey.getKeyModifiers(mods);
        if (!translationKey.equals("")) translationKey += "+";
        translationKey += InputUtil.Type.MOUSE.createFromCode(button).getTranslationKey();
        for (MacroContainer macro : macros) {
            if (!macro.onKey(translationKey)) return false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
