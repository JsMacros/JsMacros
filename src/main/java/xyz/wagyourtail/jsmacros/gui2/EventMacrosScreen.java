package xyz.wagyourtail.jsmacros.gui2;

import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        
        topbar.deftype = MacroEnum.EVENT;
        
        List<RawMacro> macros = new ArrayList<>();
        
        for (String event : Profile.registry.events) {
            if (Profile.registry.getMacros().containsKey(event))
                for (RawMacro macro : Profile.registry.getMacros().get(event).keySet()) {
                    if (macro.type == MacroEnum.EVENT) macros.add(macro);
                }
        }
        if (Profile.registry.getMacros().containsKey(""))
            for (RawMacro macro : Profile.registry.getMacros().get("").keySet()) {
                macros.add(macro);
            }
        
        Collections.sort(macros, new RawMacro.sortRawMacro());
        
        for (RawMacro macro : macros) {
            addMacro(macro);
        }
    }
}
