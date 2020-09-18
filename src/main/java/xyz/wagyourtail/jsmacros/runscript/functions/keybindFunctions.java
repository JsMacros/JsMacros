package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class keybindFunctions extends Functions {
    public static final Set<String> pressedKeys = new HashSet<>();
    
    
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
        Map<String, String> keys = new HashMap<>();
        for (KeyBinding key : ImmutableList.copyOf(mc.options.keysAll)) {
            keys.put(key.getTranslationKey(), key.getBoundKeyTranslationKey());
        }
        return keys;
    }
    
    public void setKeyBind(String bind, String key) {
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
        if (keyState) KeyBinding.onKeyPressed(keyBind);
        KeyBinding.setKeyPressed(keyBind, keyState);
    }
    
    public void keyBind(String keyBind, boolean keyState) {
        for (KeyBinding key : mc.options.keysAll) {
            if (key.getBoundKeyTranslationKey().equals(keyBind)) {
                if (keyState) KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(key.getBoundKeyTranslationKey()));
                key.setPressed(keyState);
                return;
            }
        }
    }
    
    public void key(KeyBinding keyBind, boolean keyState) {
        if (keyState) KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(keyBind.getBoundKeyTranslationKey()));
        keyBind.setPressed(keyState);
    }
    
    public List<String> getPressedKeys() {
        synchronized (pressedKeys) {
            return new ArrayList<>(pressedKeys);
        }
    }
}
