package xyz.wagyourtail.jsmacros.macros;

import java.util.HashMap;

import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;

public class KeyDownMacro extends BaseMacro {
    private int mods;
    private String key;
    private boolean prevKeyState = false;
    
    public KeyDownMacro(RawMacro macro) {
        super(macro);
        String mods = "";
        this.mods = 0;
        try {
            String[] comb = macro.eventkey.split("\\+");
            int i = 0;
            boolean notfirst = false;
            for (String key : comb) {
                if (++i == comb.length) this.key = key;
                else {
                    if (notfirst) mods += "+";
                    mods += key;
                }
            }
            this.mods = jsMacros.getModInt(mods);
        } catch(Exception e) {
            key = InputUtil.UNKNOWN_KEY.getTranslationKey();
        }
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
        if (args.get("key").equals(key) && (jsMacros.getModInt((String)args.get("mods")) & mods) == mods)
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