package xyz.wagyourtail.jsmacros.gui.screens.macros;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.event.IEventTrigger;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.RunScript;
import xyz.wagyourtail.jsmacros.gui.screens.ProfileScreen;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EventMacrosScreen extends MacroScreen {
    
    public EventMacrosScreen(Screen parent) {
        super(parent);
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        eventScreen.setColor(0x4FFFFFFF);
        
        keyScreen.onPress = (btn) -> {
            this.openParent();
        };

        profileScreen.onPress = (btn) -> {
            client.openScreen(new ProfileScreen(this));
        };
        
        topbar.updateType(IEventTrigger.TriggerType.EVENT);
        
        List<ScriptTrigger> macros = new ArrayList<>();
        
        for (String event : ImmutableList.copyOf(RunScript.eventRegistry.events)) {
            Set<IEventListener> eventListeners = RunScript.eventRegistry.getListeners(event);
            if (eventListeners != null) 
                for (IEventListener macro : ImmutableList.copyOf(eventListeners)) {
                    if (macro instanceof BaseMacro && ((BaseMacro) macro).getRawMacro().triggerType == IEventTrigger.TriggerType.EVENT) macros.add(((BaseMacro) macro).getRawMacro());
                }
        }
        if (RunScript.eventRegistry.getListeners().containsKey(""))
            for (IEventListener macro : RunScript.eventRegistry.getListeners().get("")) {
                if (macro instanceof BaseMacro) macros.add(((BaseMacro) macro).getRawMacro());
            }

        Collections.sort(macros, ConfigManager.INSTANCE.getSortComparator());
        
        for (ScriptTrigger macro : macros) {
            addMacro(macro);
        }
    }
}
