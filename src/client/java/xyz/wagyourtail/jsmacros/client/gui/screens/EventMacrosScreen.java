package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.containers.MacroListTopbar;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventMacrosScreen extends MacroScreen {

    public EventMacrosScreen(Screen parent) {
        super(parent);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        eventScreen.setColor(0x4FFFFFFF);

        ((MacroListTopbar) topbar).updateType(ScriptTrigger.TriggerType.EVENT);

        List<ScriptTrigger> macros = new ArrayList<>();

        for (String event : ImmutableList.copyOf(JsMacrosClient.clientCore.eventRegistry.events)) {
            for (IEventListener macro : JsMacrosClient.clientCore.eventRegistry.getListeners(event)) {
                if (macro instanceof BaseListener && ((BaseListener) macro).getRawTrigger().triggerType == ScriptTrigger.TriggerType.EVENT) {
                    macros.add(((BaseListener) macro).getRawTrigger());
                }
            }
        }
        if (JsMacrosClient.clientCore.eventRegistry.getListeners().containsKey("")) {
            for (IEventListener macro : JsMacrosClient.clientCore.eventRegistry.getListeners().get("")) {
                if (macro instanceof BaseListener) {
                    macros.add(((BaseListener) macro).getRawTrigger());
                }
            }
        }

        macros.sort(JsMacrosClient.clientCore.config.getOptions(ClientConfigV2.class).getSortComparator());

        for (ScriptTrigger macro : macros) {
            addMacro(macro);
        }
    }

}
