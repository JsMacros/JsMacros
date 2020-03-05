package xyz.wagyourtail.jsmacros.profile;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.*;
import xyz.wagyourtail.jsmacros.events.AirChangeCallback;
import xyz.wagyourtail.jsmacros.events.DamageCallback;
import xyz.wagyourtail.jsmacros.events.DeathCallback;
import xyz.wagyourtail.jsmacros.events.EventTypesEnum;
import xyz.wagyourtail.jsmacros.events.JoinCallback;
import xyz.wagyourtail.jsmacros.events.KeyCallback;
import xyz.wagyourtail.jsmacros.events.RecieveMessageCallback;
import xyz.wagyourtail.jsmacros.events.SendMessageCallback;
import xyz.wagyourtail.jsmacros.macros.*;

public class Profile {
    public String profileName;
    public static HashMap<EventTypesEnum, HashMap<RawMacro, BaseMacro>> macros;
    private static FabricKeyBinding keyBinding;

    public Profile(String defaultProfile) {
        loadProfile(defaultProfile);
        if (macros == null) {
            profileName = "default";
            macros = new HashMap<>();
            saveProfile();
        }
        
        keyBinding = FabricKeyBinding.Builder.create(new Identifier("jsmacros", "menu"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "JSMacros").build();
        KeyBindingRegistry.INSTANCE.addCategory("JSMacros");
        KeyBindingRegistry.INSTANCE.register(keyBinding);

        initEventHandlerCallbacks();
    }

    public void loadProfile(String pName) {
        macros = new HashMap<>();
        ArrayList<RawMacro> rawProfile = jsMacros.config.options.profiles.get(pName);
        if (rawProfile == null) {
            System.out.println("profile \"" + pName + "\" does not exist or is broken/null");
            return;
        }
        profileName = pName;
        for (RawMacro rawmacro : rawProfile) {
            addMacro(rawmacro);
        }
        
        HashMap<String, Object> args = new HashMap<>();
        args.put("profile", pName);
        if (macros.containsKey(EventTypesEnum.PROFILE_LOAD)) for (BaseMacro macro : macros.get(EventTypesEnum.PROFILE_LOAD).values()) {
            macro.trigger(EventTypesEnum.PROFILE_LOAD, args);
        }
    }

    public ArrayList<RawMacro> toRawProfile() {
        ArrayList<RawMacro> rawProf = new ArrayList<>();
        for (HashMap<RawMacro, BaseMacro> eventMacros : macros.values()) {
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

    public void addMacro(RawMacro rawmacro) {
        switch (rawmacro.type) {
            case KEY_RISING:
                macros.putIfAbsent(EventTypesEnum.KEY, new HashMap<>());
                macros.get(EventTypesEnum.KEY).put(rawmacro, new KeyDownMacro(rawmacro));
                break;
            case KEY_FALLING:
                macros.putIfAbsent(EventTypesEnum.KEY, new HashMap<>());
                macros.get(EventTypesEnum.KEY).put(rawmacro, new KeyUpMacro(rawmacro));
                break;
            case KEY_BOTH:
                macros.putIfAbsent(EventTypesEnum.KEY, new HashMap<>());
                macros.get(EventTypesEnum.KEY).put(rawmacro, new KeyBothMacro(rawmacro));
                break;
            case EVENT:
                macros.putIfAbsent(EventTypesEnum.valueOf(rawmacro.eventkey), new HashMap<>());
                macros.get(EventTypesEnum.valueOf(rawmacro.eventkey)).put(rawmacro, new EventMacro(rawmacro));
                break;
            default:
                System.out.println("Failed To Add: Unknown macro type for file " + rawmacro.scriptFile.toString());
                break;
        }
    }

    public void removeMacro(RawMacro rawmacro) {
        if (toRawProfile().contains(rawmacro) && rawmacro != null) switch (rawmacro.type) {
            case KEY_RISING:
            case KEY_FALLING:
            case KEY_BOTH:
                macros.putIfAbsent(EventTypesEnum.KEY, new HashMap<>());
                macros.get(EventTypesEnum.KEY).remove(rawmacro);
                break;
            case EVENT:
                macros.putIfAbsent(EventTypesEnum.valueOf(rawmacro.eventkey), new HashMap<>());
                macros.get(EventTypesEnum.valueOf(rawmacro.eventkey)).remove(rawmacro);
                break;
            default:
                System.out.println("Failed To Remove: Unknown macro type for file " + rawmacro.scriptFile.toString());
                break;
        }
    }

    public BaseMacro getMacro(RawMacro rawMacro) {
        for (HashMap<RawMacro, BaseMacro> eventMacros : macros.values()) {
            for (RawMacro macro : eventMacros.keySet()) {
                if (rawMacro == macro) return eventMacros.get(macro);
            }
        }
        return null;
    }
    
    public HashMap<EventTypesEnum, HashMap<RawMacro, BaseMacro>> getMacros() {
        return macros;
    }
    
    private void initEventHandlerCallbacks() {
        // -------- JOIN ---------- //

           JoinCallback.EVENT.register((conn, player) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("connection", conn);
               args.put("player", player);
               if (macros.containsKey(EventTypesEnum.JOIN_SERVER)) for (BaseMacro macro : macros.get(EventTypesEnum.JOIN_SERVER).values()) {
                   macro.trigger(EventTypesEnum.JOIN_SERVER, args);
               }
           });
           
           // ----- SEND_MESSAGE -----//
           
           SendMessageCallback.EVENT.register((message) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("message", message);
               if (macros.containsKey(EventTypesEnum.SEND_MESSAGE)) for (BaseMacro macro : macros.get(EventTypesEnum.SEND_MESSAGE).values()) {
                   try {
                       macro.trigger(EventTypesEnum.SEND_MESSAGE, args).join();
                   } catch (InterruptedException e1) {}
               }
               
               if (macros.containsKey(EventTypesEnum.ANYTHING)) for (BaseMacro macro : macros.get(EventTypesEnum.ANYTHING).values()) {
                   try {
                       macro.trigger(EventTypesEnum.SEND_MESSAGE, args).join();
                   } catch (InterruptedException e1) {}
               }
               
               message = (String) args.get("message");
               return message;
           });
           
           // ---- RECV_MESSAGE  ---- //
           
           RecieveMessageCallback.EVENT.register((message) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("message", message);
               if (macros.containsKey(EventTypesEnum.RECV_MESSAGE)) for (BaseMacro macro : macros.get(EventTypesEnum.RECV_MESSAGE).values()) {
                   try {
                       macro.trigger(EventTypesEnum.RECV_MESSAGE, args).join();
                   } catch (InterruptedException e1) {}
               }
               
               if (macros.containsKey(EventTypesEnum.ANYTHING)) for (BaseMacro macro : macros.get(EventTypesEnum.ANYTHING).values()) {
                   try {
                       macro.trigger(EventTypesEnum.RECV_MESSAGE, args).join();
                   } catch (InterruptedException e1) {}
               }
               
               message = (String) args.get("message");
               return message;
           });
           
           // -------- TICK --------- //
           
           ClientTickCallback.EVENT.register(e -> {
               if (macros.containsKey(EventTypesEnum.TICK)) for (BaseMacro macro : macros.get(EventTypesEnum.TICK).values()) {
                   macro.trigger(EventTypesEnum.TICK, new HashMap<>());
               }
           });

           // -------- KEY ----------- //

           KeyCallback.EVENT.register((window, key, scancode, action, mods) -> {
               InputUtil.KeyCode keycode;
               if (jsMacros.getMinecraft().currentScreen != null) return ActionResult.PASS;
               if (key == -1 || action == 2) return ActionResult.PASS;
               
               if (key <= 7) keycode = InputUtil.Type.MOUSE.createFromCode(key);
               else keycode = InputUtil.Type.KEYSYM.createFromCode(key);

               if (keycode == InputUtil.UNKNOWN_KEYCODE) return ActionResult.PASS;
               if (keyBinding.getBoundKey() == keycode && action == 1) jsMacros.getMinecraft().openScreen(jsMacros.macroListScreen);

               HashMap<String, Object> args = new HashMap<>();
               args.put("key", keycode);
               args.put("action", action);
               if (macros.containsKey(EventTypesEnum.KEY)) for (BaseMacro macro : macros.get(EventTypesEnum.KEY).values()) {
                   macro.trigger(EventTypesEnum.KEY, args);
               }

               if (macros.containsKey(EventTypesEnum.ANYTHING)) for (BaseMacro macro : macros.get(EventTypesEnum.ANYTHING).values()) {
                   macro.trigger(EventTypesEnum.KEY, args);
               }

               return ActionResult.PASS;
           });
           
           // ------ AIR CHANGE ------ //
           
           AirChangeCallback.EVENT.register((air) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("air", air);
               if (macros.containsKey(EventTypesEnum.AIR_CHANGE)) for (BaseMacro macro : macros.get(EventTypesEnum.AIR_CHANGE).values()) {
                   try {
                       macro.trigger(EventTypesEnum.AIR_CHANGE, args).join();
                   } catch (InterruptedException e1) {}
               }
               
               if (macros.containsKey(EventTypesEnum.ANYTHING)) for (BaseMacro macro : macros.get(EventTypesEnum.ANYTHING).values()) {
                   try {
                       macro.trigger(EventTypesEnum.AIR_CHANGE, args).join();
                   } catch (InterruptedException e1) {}
               }
           });
           
           // ------ DAMAGE -------- //
           
           DamageCallback.EVENT.register((source, health, change) -> {
               HashMap<String, Object> args = new HashMap<>();
               args.put("source", source);
               args.put("health", health);
               args.put("change", change);
               if (macros.containsKey(EventTypesEnum.DAMAGE)) for (BaseMacro macro : macros.get(EventTypesEnum.DAMAGE).values()) {
                   try {
                       macro.trigger(EventTypesEnum.DAMAGE, args).join();
                   } catch (InterruptedException e1) {}
               }
               
               if (macros.containsKey(EventTypesEnum.ANYTHING)) for (BaseMacro macro : macros.get(EventTypesEnum.ANYTHING).values()) {
                   try {
                       macro.trigger(EventTypesEnum.DAMAGE, args).join();
                   } catch (InterruptedException e1) {}
               }
           });
           
           // ------ DEATH -------- //
           
           DeathCallback.EVENT.register(() -> {
               HashMap<String, Object> args = new HashMap<>();
               if (macros.containsKey(EventTypesEnum.DEATH)) for (BaseMacro macro : macros.get(EventTypesEnum.DEATH).values()) {
                   try {
                       macro.trigger(EventTypesEnum.DEATH, args).join();
                   } catch (InterruptedException e1) {}
               }
               
               if (macros.containsKey(EventTypesEnum.ANYTHING)) for (BaseMacro macro : macros.get(EventTypesEnum.ANYTHING).values()) {
                   try {
                       macro.trigger(EventTypesEnum.DEATH, args).join();
                   } catch (InterruptedException e1) {}
               }
           });
           
       }
}