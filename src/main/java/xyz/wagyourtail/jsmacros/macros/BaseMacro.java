package xyz.wagyourtail.jsmacros.macros;

import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEventListener;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

public abstract class BaseMacro implements IEventListener {
    private final RawMacro macro;
    
    public BaseMacro(RawMacro macro) {
        this.macro = macro;
    }
    
    public RawMacro getRawMacro() {
        return macro;
    }
    
    public Thread runMacro(IEvent event) {
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