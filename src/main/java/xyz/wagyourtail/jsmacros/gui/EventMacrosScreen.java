package xyz.wagyourtail.jsmacros.gui;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IRawMacro;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEventListener;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.profile.Profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.screen.Screen;

public class EventMacrosScreen extends MacroScreen {
    
    public EventMacrosScreen(Screen parent) {
        super(parent);
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        eventScreen.setColor(0x4FFFFFFF);
        
        keyScreen.onPress = (btn) -> {
            client.openScreen(this.parent);
        };

        profileScreen.onPress = (btn) -> {
            client.openScreen(new ProfileScreen(this));
        };
        
        topbar.updateType(IRawMacro.MacroType.EVENT);
        
        List<RawMacro> macros = new ArrayList<>();
        
        for (String event : ImmutableList.copyOf(Profile.registry.events)) {
            Set<IEventListener> eventListeners = Profile.registry.getListeners(event);
            if (eventListeners != null) 
                for (IEventListener macro : ImmutableList.copyOf(eventListeners)) {
                    if (macro instanceof BaseMacro && ((BaseMacro) macro).getRawMacro().type == IRawMacro.MacroType.EVENT) macros.add(((BaseMacro) macro).getRawMacro());
                }
        }
        if (Profile.registry.getListeners().containsKey(""))
            for (IEventListener macro : Profile.registry.getListeners().get("")) {
                if (macro instanceof BaseMacro) macros.add(((BaseMacro) macro).getRawMacro());
            }

        Collections.sort(macros, jsMacros.config.getSortComparator());
        
        for (RawMacro macro : macros) {
            addMacro(macro);
        }
    }
}
