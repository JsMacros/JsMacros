package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class keybindFunctions extends Functions {
    
    public keybindFunctions(String libName) {
        super(libName);
    }
    
    public keybindFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    public Key getKeyCode(String keyName) {
        try {
            return InputUtil.fromTranslationKey(keyName);
        } catch (Exception e) {
            return InputUtil.UNKNOWN_KEY;
        }
    }
    
    public Map<String, String> getKeyBindings() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Map<String, String> keys = new HashMap<>();
        for (KeyBinding key : mc.options.keysAll) {
            keys.put(key.getTranslationKey(), key.getBoundKeyTranslationKey());
        }
        return keys;
    }
    
    public void setKeyBind(String bind, String key) {
        MinecraftClient mc = MinecraftClient.getInstance();
        for (KeyBinding keybind : mc.options.keysAll) {
            if (keybind.getBoundKeyTranslationKey().equals(bind)) {
                keybind.setBoundKey(InputUtil.fromTranslationKey(key));
                return;
            }
        }
    }
    
    public void key(String keyName, boolean keyState) {
        key(getKeyCode(keyName), keyState);
    }
    
    public void key(Key keyBind, boolean keyState) {
        KeyBinding.setKeyPressed(keyBind, keyState);
    }
    
    public void keyBind(String keyBind, boolean keyState) {
        MinecraftClient mc = MinecraftClient.getInstance();
        for (KeyBinding key : mc.options.keysAll) {
            if (key.getBoundKeyTranslationKey().equals(keyBind)) {
                key.setPressed(keyState);
                return;
            }
        }
    }
    
    public void key(KeyBinding keyBind, boolean keyState) {
        keyBind.setPressed(keyState);
    }
}
