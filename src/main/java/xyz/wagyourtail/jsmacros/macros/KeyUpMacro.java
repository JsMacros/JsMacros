package xyz.wagyourtail.jsmacros.macros;

import java.util.HashMap;

import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.events.EventTypesEnum;

public class KeyUpMacro extends BaseMacro {
    private InputUtil.KeyCode key;
    private boolean prevKeyState = false;
    
    public KeyUpMacro(RawMacro macro) {
        super(macro);
        key = InputUtil.fromName(macro.eventkey);
    }
    
    public void setKey(InputUtil.KeyCode setkey) {
        key = setkey;
    }
    
    public String getKey() {
        return key.getName();
    }
    
    public Thread trigger(EventTypesEnum type, HashMap<String, Object> args) {
        if (check(args)) {
            return runMacro(type, args);
        }
        return null;
    }
    
    private boolean check(HashMap<String, Object> args) {
        boolean keyState = false;
        if ((int)args.get("action") > 0) keyState = true;
        if ((InputUtil.KeyCode)args.get("key") == key)
            if (keyState && !prevKeyState) {
                prevKeyState = true;
                return false;
            } else if (!keyState && prevKeyState) {
                prevKeyState = false;
                return true;
            }
        return false;
    }
}