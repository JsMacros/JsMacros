package xyz.wagyourtail.jsmacros;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.access.IChatHud;
import xyz.wagyourtail.jsmacros.api.events.*;
import xyz.wagyourtail.jsmacros.api.functions.*;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.IProfile;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.RunScript;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventRegistry;
import xyz.wagyourtail.jsmacros.core.library.impl.*;
import xyz.wagyourtail.jsmacros.events.TickBasedEvents;
import xyz.wagyourtail.jsmacros.gui.screens.macros.MacroScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile implements IProfile {
    public String profileName;
    
    public Profile() {
    }
    
    public void init(String defaultProfile) {
        loadOrCreateProfile(defaultProfile);

        TickBasedEvents.init();
        
        initRegistries();
    }
    
    @Override
    public String getCurrentProfileName() {
        return profileName;
    }
    
    @Override
    public void renameCurrentProfile(String profile) {
        profileName = profile;
    }
    
    @Override
    public void logError(Throwable ex) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.inGameHud != null) {
            LiteralText text = new LiteralText(ex.toString());
            ((IChatHud)mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text);
        }
    }
    
    public EventRegistry getRegistry() {
        return RunScript.eventRegistry;
    }

    public void loadOrCreateProfile(String pName) {
        RunScript.eventRegistry.clearMacros();
        if (ConfigManager.INSTANCE.options.profiles.containsKey(pName)) {
            loadProfile(pName);
        } else {
            ConfigManager.INSTANCE.options.profiles.put(pName, new ArrayList<>());
            loadProfile(pName);
            ConfigManager.INSTANCE.saveConfig();
        }
    }

    private boolean loadProfile(String pName) {
        RunScript.eventRegistry.clearMacros();
        final List<ScriptTrigger> rawProfile = ConfigManager.INSTANCE.options.profiles.get(pName);
        if (rawProfile == null) {
            System.out.println("profile \"" + pName + "\" does not exist or is broken/null");
            return false;
        }
        profileName = pName;
        for (ScriptTrigger rawmacro : rawProfile) {
            RunScript.eventRegistry.addRawMacro(rawmacro);
        }
        final MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen instanceof MacroScreen) {
            ((MacroScreen) mc.currentScreen).reload();
        }
        Map<String, Object> args = new HashMap<>();
        args.put("profile", pName);
        new EventProfileLoad(this, pName);
        
        return true;
    }

    public void saveProfile() {
        ConfigManager.INSTANCE.options.profiles.put(profileName, RunScript.eventRegistry.getRawMacros());
        ConfigManager.INSTANCE.saveConfig();
    }
    
    public void initRegistries() {
        IProfile.super.initRegistries();
        RunScript.eventRegistry.addEvent(EventAirChange.class);
        RunScript.eventRegistry.addEvent(EventArmorChange.class);
        RunScript.eventRegistry.addEvent(EventBlockUpdate.class);
        RunScript.eventRegistry.addEvent(EventBossbar.class);
        RunScript.eventRegistry.addEvent(EventChunkLoad.class);
        RunScript.eventRegistry.addEvent(EventChunkUnload.class);
        RunScript.eventRegistry.addEvent(EventDamage.class);
        RunScript.eventRegistry.addEvent(EventDeath.class);
        RunScript.eventRegistry.addEvent(EventDimensionChange.class);
        RunScript.eventRegistry.addEvent(EventDisconnect.class);
        RunScript.eventRegistry.addEvent(EventEXPChange.class);
        RunScript.eventRegistry.addEvent(EventHeldItemChange.class);
        RunScript.eventRegistry.addEvent(EventHungerChange.class);
        RunScript.eventRegistry.addEvent(EventItemDamage.class);
        RunScript.eventRegistry.addEvent(EventItemPickup.class);
        RunScript.eventRegistry.addEvent(EventJoinServer.class);
        RunScript.eventRegistry.addEvent(EventKey.class);
        RunScript.eventRegistry.addEvent(EventOpenScreen.class);
        RunScript.eventRegistry.addEvent(EventPlayerJoin.class);
        RunScript.eventRegistry.addEvent(EventPlayerLeave.class);
        RunScript.eventRegistry.addEvent(EventProfileLoad.class);
        RunScript.eventRegistry.addEvent(EventRecvMessage.class);
        RunScript.eventRegistry.addEvent(EventSendMessage.class);
        RunScript.eventRegistry.addEvent(EventSignEdit.class);
        RunScript.eventRegistry.addEvent(EventSound.class);
        RunScript.eventRegistry.addEvent(EventTick.class);
        RunScript.eventRegistry.addEvent(EventTitle.class);
        
        RunScript.libraryRegistry.addLibrary(FChat.class);
        RunScript.libraryRegistry.addLibrary(FHud.class);
        RunScript.libraryRegistry.addLibrary(FJsMacros.class);
        RunScript.libraryRegistry.addLibrary(FKeyBind.class);
        RunScript.libraryRegistry.addLibrary(FPlayer.class);
        RunScript.libraryRegistry.addLibrary(FTime.class);
        RunScript.libraryRegistry.addLibrary(FWorld.class);
    }
    
    
    public void triggerMacro(BaseEvent event) {
        triggerMacroNoAnything(event);

        if (RunScript.eventRegistry.macros.containsKey("ANYTHING")) for (IEventListener macro : ImmutableList.copyOf(RunScript.eventRegistry.macros.get("ANYTHING"))) {
            macro.trigger(event);
        }
    }
    
    public void triggerMacroJoin(BaseEvent event) {
        triggerMacroJoinNoAnything(event);

        if (RunScript.eventRegistry.macros.containsKey("ANYTHING")) for (IEventListener macro : ImmutableList.copyOf(RunScript.eventRegistry.macros.get("ANYTHING"))) {
            try {
                Thread t = macro.trigger(event);
                if (t != null) t.join();
            } catch (InterruptedException e) {
            }
        }
    }
    
    public void triggerMacroNoAnything(BaseEvent event) {
        if (event instanceof EventCustom) {
            if (RunScript.eventRegistry.macros.containsKey(((EventCustom) event).eventName))
                for (IEventListener macro : ImmutableList.copyOf(RunScript.eventRegistry.macros.get(((EventCustom) event).eventName))) {
                    macro.trigger(event);
                }
        } else {
            if (RunScript.eventRegistry.macros.containsKey(event.getEventName()))
                for (IEventListener macro : ImmutableList.copyOf(RunScript.eventRegistry.macros.get(event.getEventName()))) {
                    macro.trigger(event);
                }
        }
    }
    
    public void triggerMacroJoinNoAnything(BaseEvent event) {
        if (event instanceof EventCustom) {
            if (RunScript.eventRegistry.macros.containsKey(((EventCustom) event).eventName))
                for (IEventListener macro : ImmutableList.copyOf(RunScript.eventRegistry.macros.get(((EventCustom) event).eventName))) {
                    try {
                        Thread t = macro.trigger(event);
                        if (t != null) t.join();
                    } catch (InterruptedException e) {
                    }
                }
        } else {
            if (RunScript.eventRegistry.macros.containsKey(event.getClass().getSimpleName()))
                for (IEventListener macro : ImmutableList.copyOf(RunScript.eventRegistry.macros.get(event.getEventName()))) {
                    try {
                        Thread t = macro.trigger(event);
                        if (t != null) t.join();
                    } catch (InterruptedException e) {
                    }
                }
        }
    }

    @Override
    @Deprecated
    public void triggerMacro(String macroname, Map<String, Object> args) {
        throw new RuntimeException("Deprecated");
    }

    @Override
    @Deprecated
    public void triggerMacroJoin(String macroname, Map<String, Object> args) {
        throw new RuntimeException("Deprecated");
    }

    @Override
    @Deprecated
    public void triggerMacroNoAnything(String macroname, Map<String, Object> args) {
        throw new RuntimeException("Deprecated");
    }

    @Override
    @Deprecated
    public void triggerMacroJoinNoAnything(String macroname, Map<String, Object> args) {
        throw new RuntimeException("Deprecated");
    }
}