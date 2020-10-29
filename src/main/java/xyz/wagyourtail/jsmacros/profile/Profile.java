package xyz.wagyourtail.jsmacros.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.api.events.*;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEventListener;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEventRegistry;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IProfile;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.events.TickBasedEvents;
import xyz.wagyourtail.jsmacros.gui.MacroScreen;

public class Profile implements IProfile {
    public String profileName;
    public static final EventRegistry registry = new EventRegistry();

    public Profile() {
    }
    
    public void init(String defaultProfile) {
        loadOrCreateProfile(defaultProfile);

        TickBasedEvents.init();
        
        initEventHandlerCallbacks();
    }
    
    public EventRegistry getRegistry() {
        return registry;
    }

    public void loadOrCreateProfile(String pName) {
        registry.clearMacros();
        if (JsMacros.config.options.profiles.containsKey(pName)) {
            loadProfile(pName);
        } else {
            JsMacros.config.options.profiles.put(pName, new ArrayList<>());
            loadProfile(pName);
            JsMacros.config.saveConfig();
        }
    }

    private boolean loadProfile(String pName) {
        registry.clearMacros();
        final List<RawMacro> rawProfile = JsMacros.config.options.profiles.get(pName);
        if (rawProfile == null) {
            System.out.println("profile \"" + pName + "\" does not exist or is broken/null");
            return false;
        }
        profileName = pName;
        for (RawMacro rawmacro : rawProfile) {
            registry.addRawMacro(rawmacro);
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
        JsMacros.config.options.profiles.put(profileName, registry.getRawMacros());
        JsMacros.config.saveConfig();
    }

    @SuppressWarnings("deprecation")
    private void initEventHandlerCallbacks() {
        registry.addEvent("ANYTHING");
        registry.addEvent("PROFILE_LOAD", EventProfileLoad.class);
        registry.addEvent("JOIN_SERVER", EventJoinServer.class);
        registry.addEvent("DISCONNECT", EventDisconnect.class);
        registry.addEvent("SEND_MESSAGE", EventSendMessage.class);
        registry.addEvent("RECV_MESSAGE", EventRecvMessage.class);
        registry.addEvent("PLAYER_JOIN", EventPlayerJoin.class);
        registry.addEvent("PLAYER_LEAVE", EventPlayerLeave.class);
        registry.addEvent("TICK", EventTick.class);
        registry.addEvent("KEY", EventKey.class);
        registry.addEvent("AIR_CHANGE", EventAirChange.class);
        registry.addEvent("DAMAGE", EventDamage.class);
        registry.addEvent("DEATH", EventDeath.class);
        registry.addEvent("ITEM_DAMAGE", EventItemDamage.class);
        registry.addEvent("HUNGER_CHANGE", EventHungerChange.class);
        registry.addEvent("DIMENSION_CHANGE", EventDimensionChange.class);
        registry.addEvent("SOUND", EventSound.class);
        registry.addEvent("OPEN_SCREEN", EventOpenScreen.class);
        registry.addEvent("TITLE", EventTitle.class);
        registry.addEvent("HELD_ITEM", EventHeldItemChange.class);
        registry.addEvent("ARMOR_CHANGE", EventArmorChange.class);
        registry.addEvent("BOSSBAR_UPDATE", EventBossbar.class);
        registry.addEvent("SIGN_EDIT", EventSignEdit.class);
        registry.addEvent("CHUNK_LOAD", EventChunkLoad.class);
        registry.addEvent("CHUNK_UNLOAD", EventChunkUnload.class);
        registry.addEvent("BLOCK_UPDATE", EventBlockUpdate.class);
        registry.addEvent("ITEM_PICKUP", EventItemPickup.class);
        registry.addEvent("EXP_CHANGE", EventEXPChange.class);
        
    }
    
    public void triggerMacro(IEvent event) {
        if (registry.macros.containsKey(event.getClass().getSimpleName())) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(event.getClass().getSimpleName()))) {
            macro.trigger(event);
        }

        if (registry.macros.containsKey("ANYTHING")) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get("ANYTHING"))) {
            macro.trigger(event);
        }
    }
    
    public void triggerMacroJoin(IEvent event) {
        if (registry.macros.containsKey(event.getClass().getSimpleName())) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(event.getClass().getSimpleName()))) {
            try {
                Thread t = macro.trigger(event);
                if (t != null) t.join();
            } catch (InterruptedException e) {
            }
        }

        if (registry.macros.containsKey("ANYTHING")) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get("ANYTHING"))) {
            try {
                Thread t = macro.trigger(event);
                if (t != null) t.join();
            } catch (InterruptedException e) {
            }
        }
    }
    
    public void triggerMacroNoAnything(IEvent event) {
        if (event instanceof EventCustom) {
            if (registry.macros.containsKey(((EventCustom) event).eventName))
                for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(((EventCustom) event).eventName))) {
                    macro.trigger(event);
                }
        } else {
            if (registry.macros.containsKey(event.getClass().getSimpleName()))
                for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(event.getClass().getSimpleName()))) {
                    macro.trigger(event);
                }
        }
    }
    
    public void triggerMacroJoinNoAnything(IEvent event) {
        if (event instanceof EventCustom) {
            if (registry.macros.containsKey(((EventCustom) event).eventName))
                for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(((EventCustom) event).eventName))) {
                    try {
                        Thread t = macro.trigger(event);
                        if (t != null) t.join();
                    } catch (InterruptedException e) {
                    }
                }
        } else {
            if (registry.macros.containsKey(event.getClass().getSimpleName()))
                for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(event.getClass().getSimpleName()))) {
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