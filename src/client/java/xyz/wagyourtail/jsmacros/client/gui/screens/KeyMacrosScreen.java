package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KeyMacrosScreen extends MacroScreen {

    public KeyMacrosScreen(Screen parent) {
        super(parent);
    }

    @Override
    public void init() {
        super.init();
        keyScreen.setColor(0x4FFFFFFF);

        Set<IEventListener> listeners = JsMacrosClient.clientCore.eventRegistry.getListeners().get(EventKey.class.getAnnotation(Event.class).value());
        List<ScriptTrigger> macros = new ArrayList<>();

        if (listeners != null) {
            for (IEventListener event : ImmutableList.copyOf(listeners)) {
                if (event instanceof BaseListener && ((BaseListener) event).getRawTrigger().triggerType != ScriptTrigger.TriggerType.EVENT) {
                    macros.add(((BaseListener) event).getRawTrigger());
                }
            }
        }

        macros.sort(JsMacrosClient.clientCore.config.getOptions(ClientConfigV2.class).getSortComparator());

        for (ScriptTrigger macro : macros) {
            addMacro(macro);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        String translationKey = EventKey.getKeyModifiers(modifiers);
        if (!translationKey.equals("")) {
            translationKey += "+";
        }
        translationKey += InputUtil.fromKeyCode(keyCode, scanCode).getTranslationKey();
        for (MacroContainer macro : (List<MacroContainer>) (List) macros) {
            if (!macro.onKey(translationKey)) {
                return false;
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        int mods = 0;
        if (hasShiftDown()) {
            mods += 1;
        }
        if (hasControlDown()) {
            mods += 2;
        }
        if (hasAltDown()) {
            mods += 4;
        }
        String translationKey = EventKey.getKeyModifiers(mods);
        if (!translationKey.equals("")) {
            translationKey += "+";
        }
        translationKey += InputUtil.Type.MOUSE.createFromCode(button).getTranslationKey();
        for (MacroContainer macro : (List<MacroContainer>) (List) macros) {
            if (!macro.onKey(translationKey)) {
                return false;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

}
