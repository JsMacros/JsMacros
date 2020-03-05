package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.KeyCode;

public class keybindFunctions {
    
    
    public KeyCode getKeyCode(String keyName) {
        return InputUtil.fromName(keyName);
    }
    
    public void key(String keyName, boolean keyState) {
        key(getKeyCode(keyName), keyState);
    }
    
    public void key(KeyCode keyBind, boolean keyState) {
        KeyBinding.setKeyPressed(keyBind, keyState);
    }
    
    public void key(KeyBinding keyBind, boolean keyState) {
        keyBind.setPressed(keyState);
    }
}
