package xyz.wagyourtail.jsmacros.profile;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.*;
import xyz.wagyourtail.jsmacros.events.AirChangeCallback;
import xyz.wagyourtail.jsmacros.events.DamageCallback;
import xyz.wagyourtail.jsmacros.events.DeathCallback;
import xyz.wagyourtail.jsmacros.events.ItemDamageCallback;
import xyz.wagyourtail.jsmacros.events.JoinCallback;
import xyz.wagyourtail.jsmacros.events.KeyCallback;
import xyz.wagyourtail.jsmacros.events.RecieveMessageCallback;
import xyz.wagyourtail.jsmacros.events.SendMessageCallback;
import xyz.wagyourtail.jsmacros.macros.*;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;
import xyz.wagyourtail.jsmacros.reflector.PlayerEntityHelper;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

public class Profile {
    public String profileName;
    public static MacroRegistry registry = new MacroRegistry();
    private static KeyBinding keyBinding;

    public Profile(String defaultProfile) {
        loadOrCreateProfile(defaultProfile);
        
        keyBinding = new KeyBinding("jsmacros.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "JSMacros");
        KeyBindingHelper.registerKeyBinding(keyBinding);

        initEventHandlerCallbacks();
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
        ArrayList<RawMacro> rawProfile = jsMacros.config.options.profiles.get(pName);
        if (rawProfile == null) {
            System.out.println("profile \"" + pName + "\" does not exist or is broken/null");
            return false;
        }
        profileName = pName;
        for (RawMacro rawmacro : rawProfile) {
            registry.addMacro(rawmacro);
        }
        
        HashMap<String, Object> args = new HashMap<>();
        args.put("profile", pName);
        if (registry.macros.containsKey("PROFILE_LOAD")) for (BaseMacro macro : registry.macros.get("PROFILE_LOAD").values()) {
            macro.trigger("PROFILE_LOAD", args);
        }
        return true;
    }

    public ArrayList<RawMacro> toRawProfile() {
        ArrayList<RawMacro> rawProf = new ArrayList<>();
        for (HashMap<RawMacro, BaseMacro> eventMacros : registry.macros.values()) {
            for (RawMacro macro : eventMacros.keySet()) {
                rawProf.add(macro);
            }
        }
        return rawProf;
    }

    public void saveProfile() {
        jsMacros.config.options.profiles.put(profileName, toRawProfile());
        jsMacros.config.saveConfig();
    }

    @Deprecated
    public void addMacro(RawMacro rawmacro) {
        registry.addMacro(rawmacro);
    }

    @Deprecated
    public void removeMacro(RawMacro rawmacro) {
        if (toRawProfile().contains(rawmacro) && rawmacro != null) registry.removeMacro(rawmacro);
    }

    @Deprecated
    public BaseMacro getMacro(RawMacro rawMacro) {
        return registry.getMacro(rawMacro);
    }
    
    @Deprecated
    public HashMap<String, HashMap<RawMacro, BaseMacro>> getMacros() {
        return registry.macros;
    }
    
    private void initEventHandlerCallbacks() {
           // -------- JOIN ---------- //
           registry.addEvent("JOIN_SERVER");
           JoinCallback.EVENT.register((conn, player) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("address", conn.getAddress().toString());
               args.put("player", new PlayerEntityHelper(player));
               if (registry.macros.containsKey("JOIN_SERVER")) for (BaseMacro macro : registry.macros.get("JOIN_SERVER").values()) {
                   macro.trigger("JOIN_SERVER", args);
               }
           });
           
           // ----- SEND_MESSAGE -----//
           registry.addEvent("SEND_MESSAGE");
           SendMessageCallback.EVENT.register((message) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("message", message);
               if (registry.macros.containsKey("SEND_MESSAGE")) for (BaseMacro macro : registry.macros.get("SEND_MESSAGE").values()) {
                   try {
                       Thread t =  macro.trigger("SEND_MESSAGE", args);
                       if (t != null) t.join();
                   } catch (InterruptedException e1) {}
               }
               
               if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                   try {
                       Thread t =  macro.trigger("SEND_MESSAGE", args);
                       if (t != null) t.join();
                   } catch (InterruptedException e1) {}
               }
               
               message = (String) args.get("message");
               return message;
           });
           
           // ---- RECV_MESSAGE  ---- //
           registry.addEvent("RECV_MESSAGE");
           RecieveMessageCallback.EVENT.register((message) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("message", message);
               if (registry.macros.containsKey("RECV_MESSAGE")) for (BaseMacro macro : registry.macros.get("RECV_MESSAGE").values()) {
                   try {
                       Thread t = macro.trigger("RECV_MESSAGE", args);
                       if (t != null) t.join();
                   } catch (InterruptedException e1) {}
               }
               
               if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                   try {
                       Thread t = macro.trigger("RECV_MESSAGE", args);
                       if (t != null) t.join();
                   } catch (InterruptedException e1) {}
               }
               
               message = (TextHelper) args.get("message");
               return message;
           });
           
           // -------- TICK --------- //
           registry.addEvent("TICK");
           ClientTickEvents.END_CLIENT_TICK.register(e -> {
               if (registry.macros.containsKey("TICK")) for (BaseMacro macro : registry.macros.get("TICK").values()) {
                   macro.trigger("TICK", new HashMap<>());
               }
           });

           // -------- KEY ----------- //
           registry.addEvent("KEY");
           KeyCallback.EVENT.register((window, key, scancode, action, mods) -> {
               InputUtil.Key keycode;
               MinecraftClient mc = MinecraftClient.getInstance();
               if (mc.currentScreen != null) return ActionResult.PASS;
               if (key == -1 || action == 2) return ActionResult.PASS;
               
               if (key <= 7) keycode = InputUtil.Type.MOUSE.createFromCode(key);
               else keycode = InputUtil.Type.KEYSYM.createFromCode(key);

               if (keycode == InputUtil.UNKNOWN_KEY) return ActionResult.PASS;
               if (keyBinding.matchesKey(key, scancode) && action == 1) mc.openScreen(jsMacros.keyMacrosScreen);

               HashMap<String, Object> args = new HashMap<>();
               args.put("rawkey", keycode);
               args.put("key", keycode.getTranslationKey());
               args.put("action", action);
               if (registry.macros.containsKey("KEY")) for (BaseMacro macro : registry.macros.get("KEY").values()) {
                   macro.trigger("KEY", args);
               }

               if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                   macro.trigger("KEY", args);
               }

               return ActionResult.PASS;
           });
           
           // ------ AIR CHANGE ------ //
           registry.addEvent("AIR_CHANGE");
           AirChangeCallback.EVENT.register((air) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("air", air);
               if (registry.macros.containsKey("AIR_CHANGE")) for (BaseMacro macro : registry.macros.get("AIR_CHANGE").values()) {
                   try {
                       macro.trigger("AIR_CHANGE", args).join();
                   } catch (InterruptedException e1) {}
               }
               
               if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                   try {
                       macro.trigger("AIR_CHANGE", args).join();
                   } catch (InterruptedException e1) {}
               }
           });
           
           // ------ DAMAGE -------- //
           registry.addEvent("DAMAGE");
           DamageCallback.EVENT.register((source, health, change) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("source", source.getName());
               args.put("health", health);
               args.put("change", change);
               if (registry.macros.containsKey("DAMAGE")) for (BaseMacro macro : registry.macros.get("DAMAGE").values()) {
                   try {
                       macro.trigger("DAMAGE", args).join();
                   } catch (InterruptedException e1) {}
               }
               
               if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                   try {
                       macro.trigger("DAMAGE", args).join();
                   } catch (InterruptedException e1) {}
               }
           });
           
           // ------ DEATH -------- //
           registry.addEvent("DEATH");
           DeathCallback.EVENT.register(() -> {
               HashMap<String, Object> args = new HashMap<>();
               if (registry.macros.containsKey("DEATH")) for (BaseMacro macro : registry.macros.get("DEATH").values()) {
                   try {
                       macro.trigger("DEATH", args).join();
                   } catch (InterruptedException e1) {}
               }
               
               if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                   try {
                       macro.trigger("DEATH", args).join();
                   } catch (InterruptedException e1) {}
               }
           });
           
           // ----- ITEM DAMAGE ----- //
           registry.addEvent("ITEM_DAMAGE");
           ItemDamageCallback.EVENT.register((stack, damage) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("stack", new ItemStackHelper(stack));
               args.put("damage", damage);
               if (registry.macros.containsKey("ITEM_DAMAGE")) for (BaseMacro macro : registry.macros.get("ITEM_DAMAGE").values()) {
                   try {
                       macro.trigger("ITEM_DAMAGE", args).join();
                   } catch (InterruptedException e1) {}
               }
               
               if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                   try {
                       macro.trigger("ITEM_DAMAGE", args).join();
                   } catch (InterruptedException e1) {}
               }
           });
           
       }
}