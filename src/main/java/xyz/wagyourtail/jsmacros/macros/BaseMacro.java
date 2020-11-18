package xyz.wagyourtail.jsmacros.macros;

import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.RunScript;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

public abstract class BaseMacro implements IEventListener {
    private final ScriptTrigger macro;
    
    public BaseMacro(ScriptTrigger macro) {
        this.macro = macro;
    }
    
    public ScriptTrigger getRawMacro() {
        return macro;
    }
    
    public Thread runMacro(BaseEvent event) {
        if (macro.enabled) {
            try {
                return RunScript.exec(macro, event);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    public boolean equals(Object o) {
        if (o instanceof BaseMacro) {
            return macro.equals(((BaseMacro)o).macro);
        }
        return super.equals(o);
    }
    
    public String toString() {
        return macro.toString().substring(3);
    }
}