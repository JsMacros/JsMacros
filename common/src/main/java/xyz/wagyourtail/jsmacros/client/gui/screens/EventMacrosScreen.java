package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.containers.MacroListTopbar;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventMacrosScreen extends MacroScreen {
    
    public EventMacrosScreen(GuiScreen parent) {
        super(parent);
        this.parent = parent;
    }
    
    @Override
    public void init() {
        super.init();
        eventScreen.setColor(0x4FFFFFFF);
        
        keyScreen.onPress = (btn) -> this.openParent();

        ((MacroListTopbar)topbar).updateType(ScriptTrigger.TriggerType.EVENT);
        
        List<ScriptTrigger> macros = new ArrayList<>();
        
        for (String event : ImmutableList.copyOf(Core.getInstance().eventRegistry.events)) {
                for (IEventListener macro : Core.getInstance().eventRegistry.getListeners(event)) {
                    if (macro instanceof BaseListener && ((BaseListener) macro).getRawTrigger().triggerType == ScriptTrigger.TriggerType.EVENT) macros.add(((BaseListener) macro).getRawTrigger());
                }
        }
        if (Core.getInstance().eventRegistry.getListeners().containsKey(""))
            for (IEventListener macro : Core.getInstance().eventRegistry.getListeners().get("")) {
                if (macro instanceof BaseListener) macros.add(((BaseListener) macro).getRawTrigger());
            }

        macros.sort(Core.getInstance().config.getOptions(ClientConfigV2.class).getSortComparator());
        
        for (ScriptTrigger macro : macros) {
            addMacro(macro);
        }
    }
}
