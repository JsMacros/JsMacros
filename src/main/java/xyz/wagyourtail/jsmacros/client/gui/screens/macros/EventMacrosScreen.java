package xyz.wagyourtail.jsmacros.client.gui.screens.macros;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.screens.ProfileScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;

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
        
        topbar.updateType(ScriptTrigger.TriggerType.EVENT);
        
        List<ScriptTrigger> macros = new ArrayList<>();
        
        for (String event : ImmutableList.copyOf(Core.instance.eventRegistry.events)) {
            Set<IEventListener> eventListeners = Core.instance.eventRegistry.getListeners(event);
            if (eventListeners != null) 
                for (IEventListener macro : ImmutableList.copyOf(eventListeners)) {
                    if (macro instanceof BaseListener && ((BaseListener) macro).getRawTrigger().triggerType == ScriptTrigger.TriggerType.EVENT) macros.add(((BaseListener) macro).getRawTrigger());
                }
        }
        if (Core.instance.eventRegistry.getListeners().containsKey(""))
            for (IEventListener macro : Core.instance.eventRegistry.getListeners().get("")) {
                if (macro instanceof BaseListener) macros.add(((BaseListener) macro).getRawTrigger());
            }

        Collections.sort(macros, JsMacros.core.config.options.getSortComparator());
        
        for (ScriptTrigger macro : macros) {
            addMacro(macro);
        }
    }
}
