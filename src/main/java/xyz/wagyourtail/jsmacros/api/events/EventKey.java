package xyz.wagyourtail.jsmacros.api.events;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.api.functions.FKeyBind;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventKey implements IEvent {
    private static KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("jsmacros.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, I18n.translate("jsmacros.title")));
    public final int action;
    public final String key;
    public final String mods;
    
    public EventKey(int key, int scancode, int action, int mods) {
        
        InputUtil.Key keycode;
        if (key <= 7) keycode = InputUtil.Type.MOUSE.createFromCode(key);
        else keycode = InputUtil.Type.KEYSYM.createFromCode(key);
        
        this.action = action;
        this.key = keycode.getTranslationKey();
        this.mods = getKeyModifiers(mods);
        
        
        if (keyBinding.matchesKey(key, scancode) && action == 1 && mc.currentScreen == null) {
            mc.openScreen(JsMacros.keyMacrosScreen);
            return;
        }
        
        if (keycode == InputUtil.UNKNOWN_KEY) return;
        
        synchronized (FKeyBind.pressedKeys) {
            if (action == 1) FKeyBind.pressedKeys.add(keycode.getTranslationKey());
            else FKeyBind.pressedKeys.remove(keycode.getTranslationKey());
        }

        if (mc.currentScreen != null && JsMacros.config.options.disableKeyWhenScreenOpen) return;
        
        Map<String, Object> args = new HashMap<>();
        args.put("rawkey", keycode);
        if (action == 1) {
            if (key == 340 || key == 344) mods -= 1;
            else if (key == 341 || key == 345) mods -= 2;
            else if (key == 342 || key == 346) mods -= 4;
        }
        
        profile.triggerMacro(this);
        
    }

    public String toString() {
        return String.format("%s:{\"key\": \"%s\"}", this.getEventName(), key);
    }
    

    
    /**
     * turn an {@link java.lang.Integer Integer} for key modifiers into a Translation Key. 
     * @param mods
     * @return
     */
    public static String getKeyModifiers(int mods) {
        String s = "";
        if ((mods & 1) == 1) {
            s += "key.keyboard.left.shift";
        }
        if ((mods & 2) == 2) {
            if (s.length() > 0) s += "+";
            s += "key.keyboard.left.control";
        }
        if ((mods & 4) == 4) {
            if (s.length() > 0) s += "+";
            s += "key.keyboard.left.alt";
        }
        return s;
    }
    
    /**
     * turn a Translation Key for modifiers into an {@link java.lang.Integer Integer}. 
     * @param mods
     * @return
     */
    public static int getModInt(String mods) {
        int i = 0;
        String[] modArr = mods.split("\\+");
        for (String mod : modArr) {
            switch (mod) {
                case "key.keyboard.left.shift":
                case "key.keyboard.right.shift":
                    i |= 1;
                    break;
                case "key.keyboard.left.control":
                case "key.keyboard.right.control":
                    i |= 2;
                    break;
                case "key.keyboard.left.alt":
                case "key.keyboard.right.alt":
                    i |= 4;
                    break;
                default:
            }
        }
        return i;
        
    }
}
