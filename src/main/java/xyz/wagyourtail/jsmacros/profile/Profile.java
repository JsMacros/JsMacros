package xyz.wagyourtail.jsmacros.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.events.*;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.macros.IEventListener;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

public class Profile {
    public String profileName;
    public static EventRegistry registry = new EventRegistry();
    private static KeyBinding keyBinding;

    public Profile(String defaultProfile) {
        loadOrCreateProfile(defaultProfile);
        keyBinding = new KeyBinding("jsmacros.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, I18n.translate("jsmacros.title"));
        KeyBindingHelper.registerKeyBinding(keyBinding);

        TickBasedEvents.init();
        
        initEventHandlerCallbacks();
    }
    
    public EventRegistry getRegistry() {
        return registry;
    }

    public void loadOrCreateProfile(String pName) {
        registry.clearMacros();
        if (jsMacros.config.options.profiles.containsKey(pName)) {
            loadProfile(pName);
        } else {
            jsMacros.config.options.profiles.put(pName, new ArrayList<>());
            loadProfile(pName);
            jsMacros.config.saveConfig();
        }
    }

    private boolean loadProfile(String pName) {
        registry.clearMacros();
        List<RawMacro> rawProfile = jsMacros.config.options.profiles.get(pName);
        if (rawProfile == null) {
            System.out.println("profile \"" + pName + "\" does not exist or is broken/null");
            return false;
        }
        profileName = pName;
        for (RawMacro rawmacro : rawProfile) {
            registry.addRawMacro(rawmacro);
        }

        Map<String, Object> args = new HashMap<>();
        args.put("profile", pName);
        triggerMacroNoAnything("PROFILE_LOAD", args);
        
        return true;
    }

    @Deprecated
    public List<RawMacro> toRawProfile() {
        return registry.getRawMacros();
    }

    public void saveProfile() {
        jsMacros.config.options.profiles.put(profileName, registry.getRawMacros());
        jsMacros.config.saveConfig();
    }

    @Deprecated
    public void addMacro(RawMacro rawmacro) {
        registry.addRawMacro(rawmacro);
    }

    @Deprecated
    public void removeMacro(RawMacro rawmacro) {
        if (toRawProfile().contains(rawmacro) && rawmacro != null) registry.removeRawMacro(rawmacro);
    }

    @Deprecated
    public BaseMacro getMacro(RawMacro rawMacro) {
        return registry.getMacro(rawMacro);
    }

    @Deprecated
    public Map<String, List<IEventListener>> getMacros() {
        return registry.macros;
    }

    private void initEventHandlerCallbacks() {
        registry.addEvent("ANYTHING");
        
        // -------- JOIN ---------- //
        registry.addEvent("JOIN_SERVER");
        JoinCallback.EVENT.register((address, player) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("address", address);
            args.put("player", player);

            triggerMacro("JOIN_SERVER", args);
        });
        
        // ----- DISCONNECT ------ // 
        registry.addEvent("DISCONNECT");
        DisconnectCallback.EVENT.register(() -> {
            Map<String, Object> args = new HashMap<>();

            triggerMacro("DISCONNECT", args);
        });
        
        // ----- SEND_MESSAGE -----//
        registry.addEvent("SEND_MESSAGE");
        SendMessageCallback.EVENT.register((message) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("message", message);

            triggerMacroJoin("SEND_MESSAGE", args);

            message = (String) args.get("message");
            return message;
        });

        // ---- RECV_MESSAGE ---- //
        registry.addEvent("RECV_MESSAGE");
        RecieveMessageCallback.EVENT.register((message) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("message", message);

            triggerMacroJoin("RECV_MESSAGE", args);

            message = (TextHelper) args.get("message");
            return message;
        });

        // ----- PLAYER JOIN ----- //
        registry.addEvent("PLAYER_JOIN");
        PlayerJoinCallback.EVENT.register((uuid, pName) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("uuid", uuid.toString());
            args.put("player", pName);

            triggerMacro("PLAYER_JOIN", args);
        });
        
        // ---- PLAYER LEAVE ----- //
        registry.addEvent("PLAYER_LEAVE");
        PlayerLeaveCallback.EVENT.register((uuid, pName) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("uuid", uuid.toString());
            args.put("player", pName);

            triggerMacro("PLAYER_LEAVE", args);
        });
        
        // -------- TICK --------- //
        registry.addEvent("TICK");
        ClientTickEvents.END_CLIENT_TICK.register(e -> {
            triggerMacroNoAnything("TICK", new HashMap<>());
        });

        // -------- KEY ----------- //
        registry.addEvent("KEY");
        KeyCallback.EVENT.register((window, key, scancode, action, mods) -> {
            InputUtil.Key keycode;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.currentScreen != null && jsMacros.config.options.disableKeyWhenScreenOpen) return ActionResult.PASS;
            if (key == -1 || action == 2) return ActionResult.PASS;

            if (key <= 7) keycode = InputUtil.Type.MOUSE.createFromCode(key);
            else keycode = InputUtil.Type.KEYSYM.createFromCode(key);

            if (keycode == InputUtil.UNKNOWN_KEY) return ActionResult.PASS;
            if (keyBinding.matchesKey(key, scancode) && action == 1 && mc.currentScreen == null) mc.openScreen(jsMacros.keyMacrosScreen);

            Map<String, Object> args = new HashMap<>();
            args.put("rawkey", keycode);
            if (action == 1) {
                if (key == 340 || key == 344) mods -= 1;
                else if (key == 341 || key == 345) mods -= 2;
                else if (key == 342 || key == 346) mods -= 4;
            }
            args.put("mods", jsMacros.getKeyModifiers(mods));
            args.put("key", keycode.getTranslationKey());
            args.put("action", action);

            triggerMacro("KEY", args);

            return ActionResult.PASS;
        });
        
        // ------ AIR CHANGE ------ //
        registry.addEvent("AIR_CHANGE");
        AirChangeCallback.EVENT.register((air) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("air", air);

            triggerMacro("AIR_CHANGE", args);
        });

        // ------ DAMAGE -------- //
        registry.addEvent("DAMAGE");
        DamageCallback.EVENT.register((source, health, change) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("source", source.getName());
            args.put("health", health);
            args.put("change", change);

            triggerMacro("DAMAGE", args);
        });

        // ------ DEATH -------- //
        registry.addEvent("DEATH");
        DeathCallback.EVENT.register(() -> {
            Map<String, Object> args = new HashMap<>();

            triggerMacro("DEATH", args);
        });

        // ----- ITEM DAMAGE ----- //
        registry.addEvent("ITEM_DAMAGE");
        ItemDamageCallback.EVENT.register((stack, damage) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("stack", stack);
            args.put("damage", damage);

            triggerMacro("ITEM_DAMAGE", args);
        });

        // ----- HUNGER CHANGE ------ //
        registry.addEvent("HUNGER_CHANGE");
        HungerChangeCallback.EVENT.register((foodLevel) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("foodLevel", foodLevel);

            triggerMacro("HUNGER_CHANGE", args);
        });

        // ----- DIMENSION CHANGE --- //
        registry.addEvent("DIMENSION_CHANGE");
        DimensionChangeCallback.EVENT.register((dim) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("dimension", dim);

            triggerMacro("DIMENSION_CHANGE", args);
        });

        
        // ------ SOUND ------ //
        registry.addEvent("SOUND");
        SoundCallback.EVENT.register((sound) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("sound", sound);

            triggerMacro("SOUND", args);
        });

        
        // ------- OPEN SCREEN ------ //
        registry.addEvent("OPEN_SCREEN");
        OpenScreenCallback.EVENT.register((screen) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("screen", screen);

            triggerMacro("OPEN_SCREEN", args);
        });
        
        // ------- TITLE ------- //
        registry.addEvent("TITLE");
        TitleCallback.EVENT.register((type, message) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("type", type);
            args.put("message", message);

            triggerMacro("HELD_ITEM", args);
        });
        
        // ----- HELD ITEM ----- //
        registry.addEvent("HELD_ITEM");
        HeldItemCallback.EVENT.register((item, oldItem, offhand) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("item", item);
            args.put("oldItem", oldItem);
            args.put("offhand", offhand);
            
            triggerMacro("HELD_ITEM", args);
        });

        // ---- ARMOR CHANGE ---- //
        registry.addEvent("ARMOR_CHANGE");
        ArmorChangeCallback.EVENT.register((slot, item, oldItem) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("item", item);
            args.put("oldItem", oldItem);
            args.put("slot", slot);
            
            triggerMacro("ARMOR_CHANGE", args);
        });
        
        // ---- BOSSBAR UPDATE ---- //
        registry.addEvent("BOSSBAR_UPDATE");
        BossBarCallback.EVENT.register((type, uuid, bossBar) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("type", type);
            args.put("bossBar", bossBar);
            args.put("uuid", uuid);
            
            triggerMacro("BOSSBAR_UPDATE", args);
        });
        
        // ---- SIGN EDIT ------ //
        registry.addEvent("SIGN_EDIT");
        SignEditCallback.EVENT.register((lines, x, y, z) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("lines", lines);
            args.put("x", x);
            args.put("y", y);
            args.put("z", z);
            args.put("close", false);
            
            triggerMacroJoinNoAnything("SIGN_EDIT", args);
            
            return (boolean) args.get("close");
        });
        
        // ---- CHUNK LOAD ----- //
        registry.addEvent("CHUNK_LOAD");
        ChunkLoadCallback.EVENT.register((x, z) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("x", x);
            args.put("z", z);
            
            triggerMacro("CHUNK_LOAD", args);
        });
        
        // ---- CHUNK UNLOAD ----- //
        registry.addEvent("CHUNK_UNLOAD");
        ChunkUnloadCallback.EVENT.register((x, z) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("x", x);
            args.put("z", z);
            
            triggerMacro("CHUNK_UNLOAD", args);
        });
        
        // ---- BLOCK UPDATE ----- //
        registry.addEvent("BLOCK_UPDATE");
        BlockUpdateCallback.EVENT.register((b) -> {
            Map<String, Object> args = new HashMap<>();
            args.put("block", b);
            
            triggerMacro("BLOCK_UPDATE", args);
        });
    }
    
    public void triggerMacro(String macroname, Map<String, Object> args) {
        if (registry.macros.containsKey(macroname)) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(macroname))) {
            macro.trigger(macroname, args);
        }

        if (registry.macros.containsKey("ANYTHING")) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get("ANYTHING"))) {
            macro.trigger(macroname, args);
        }
    }
    
    public void triggerMacroJoin(String macroname, Map<String, Object> args) {
        if (registry.macros.containsKey(macroname)) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(macroname))) {
            try {
                Thread t = macro.trigger(macroname, args);
                if (t != null) t.join();
            } catch (InterruptedException e) {
            }
        }

        if (registry.macros.containsKey("ANYTHING")) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get("ANYTHING"))) {
            try {
                Thread t = macro.trigger(macroname, args);
                if (t != null) t.join();
            } catch (InterruptedException e) {
            }
        }
    }
    
    public void triggerMacroNoAnything(String macroname, Map<String, Object> args) {
        if (registry.macros.containsKey(macroname)) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(macroname))) {
            macro.trigger(macroname, args);
        }
    }
    
    public void triggerMacroJoinNoAnything(String macroname, Map<String, Object> args) {
        if (registry.macros.containsKey(macroname)) for (IEventListener macro : ImmutableList.copyOf(registry.macros.get(macroname))) {
            try {
                Thread t = macro.trigger(macroname, args);
                if (t != null) t.join();
            } catch (InterruptedException e) {
            }
        }
    }
}