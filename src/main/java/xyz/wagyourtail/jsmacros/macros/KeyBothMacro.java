package xyz.wagyourtail.jsmacros.macros;

import java.util.HashMap;

import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.config.RawMacro;

public class KeyBothMacro extends BaseMacro {
    private InputUtil.Key key;
    private boolean prevKeyState = false;
    
    public KeyBothMacro(RawMacro macro) {
        super(macro);
        try {
            key = InputUtil.fromTranslationKey(macro.eventkey);
        } catch(Exception e) {
            key = InputUtil.UNKNOWN_KEY;
        }
    }
    
    public void setKey(InputUtil.Key setkey) {
        key = setkey;
    }
    
    public String getKey() {
        return key.getTranslationKey();
    }
    
    @Override
    public Thread trigger(String type, HashMap<String, Object> args) {
        if (check(args)) {
            return runMacro(type, args);
        }
        return null;
    }
    
    private boolean check(HashMap<String, Object> args) {
        boolean keyState = false;
        if ((int)args.get("action") > 0) keyState = true;
        if ((InputUtil.Key)args.get("rawkey") == key)
            if (keyState && !prevKeyState) {
                prevKeyState = true;
                return true;
            } else if (!keyState && prevKeyState) {
                prevKeyState = false;
                return true;
            }
        return false;
    }
}