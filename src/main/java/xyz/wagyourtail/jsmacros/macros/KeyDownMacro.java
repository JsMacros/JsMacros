package xyz.wagyourtail.jsmacros.macros;

import java.util.HashMap;

import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.config.RawMacro;

public class KeyDownMacro extends BaseMacro {
    private InputUtil.Key key;
    private boolean prevKeyState = false;
    
    public KeyDownMacro(RawMacro macro) {
        super(macro);
        key = InputUtil.fromTranslationKey(macro.eventkey);
    }
    
    public void setKey(InputUtil.Key setkey) {
        key = setkey;
    }
    
    public String getKey() {
        return key.getTranslationKey();
    }
    
    public Thread trigger(String type, HashMap<String, Object> args) {
        if (check(args)) {
            return runMacro(type, args);
        }
        return null;
    }
    
    private boolean check(HashMap<String, Object> args) {
        boolean keyState = false;
        if ((int)args.get("action") > 0) keyState = true;
        if ((InputUtil.Key)args.get("key") == key)
            if (keyState && !prevKeyState) {
                prevKeyState = true;
                return true;
            } else if (!keyState && prevKeyState) {
                prevKeyState = false;
                return false;
            }
        return false;
    }
}