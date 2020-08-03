package xyz.wagyourtail.jsmacros.macros;

import java.util.Map;

import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;

public class KeyUpMacro extends BaseMacro {
    private int mods;
    private String key;
    
    public KeyUpMacro(RawMacro macro) {
        super(macro);
        String mods = "";
        this.mods = 0;
        try {
            String[] comb = macro.eventkey.split("\\+");
            int i = 0;
            for (String key : comb) {
                if (++i == comb.length) this.key = key;
                else {
                    if (i > 1) mods += "+";
                    mods += key;
                }
            }
            this.mods = jsMacros.getModInt(mods);
        } catch(Exception e) {
            key = InputUtil.UNKNOWN_KEY.getTranslationKey();
        }
    }
    
    @Override
    public Thread trigger(String type, Map<String, Object> args) {
        if (check(args)) {
            return runMacro(type, args);
        }
        return null;
    }
    
    private boolean check(Map<String, Object> args) {
        boolean keyState = (int)args.get("action") == 1;
        if (args.get("key").equals(key) && jsMacros.getModInt((String)args.get("mods")) == mods)
            if (keyState) {
                return false;
            } else if (!keyState) {
                return true;
            }
        return false;
    }
}