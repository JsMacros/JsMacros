package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class keybindFunctions {
    
    
    public Key getKeyCode(String keyName) {
        try {
            return InputUtil.fromTranslationKey(keyName);
        } catch (Exception e) {
            return InputUtil.UNKNOWN_KEY;
        }
    }
    
    public void key(String keyName, boolean keyState) {
        key(getKeyCode(keyName), keyState);
    }
    
    public void key(Key keyBind, boolean keyState) {
        KeyBinding.setKeyPressed(keyBind, keyState);
    }
    
    public void key(KeyBinding keyBind, boolean keyState) {
        keyBind.setPressed(keyState);
    }
}
