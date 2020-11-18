package xyz.wagyourtail.jsmacros.macros;

import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.api.events.EventKey;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

public class KeyMacro extends BaseMacro {
    private int mods;
    private String key;
    
    public KeyMacro(ScriptTrigger macro) {
        super(macro);
        String mods = "";
        this.mods = 0;
        try {
            String[] comb = macro.event.split("\\+");
            int i = 0;
            for (String key : comb) {
                if (++i == comb.length) this.key = key;
                else {
                    if (i > 1) mods += "+";
                    mods += key;
                }
            }
            this.mods = EventKey.getModInt(mods);
        } catch(Exception e) {
            key = InputUtil.UNKNOWN_KEY.getTranslationKey();
        }
    }
    
    @Override
    public Thread trigger(BaseEvent event) {
        if (check((EventKey) event)) {
            return runMacro(event);
        }
        return null;
    }
    
    private boolean check(EventKey event) {
        boolean keyState = (int)event.action == 1;
        if (event.key.equals(key) && EventKey.getModInt((String)event.mods) == mods) {
            switch(getRawMacro().triggerType) {
                case KEY_FALLING:
                    return !keyState;
                case KEY_RISING:
                    return keyState;
                case EVENT:
                case KEY_BOTH:
                default:
                    return true;
            }
        }
        return false;
    }
}