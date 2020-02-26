package xyz.wagyourtail.jsmacros.macros;

import java.io.File;
import java.util.HashMap;

import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.events.EventTypesEnum;
import xyz.wagyourtail.jsmacros.runscript.RunScript;

public abstract class BaseMacro {
    private final RawMacro macro;
    
    public BaseMacro(RawMacro macro) {
        this.macro = macro;
    }
    
    public File getFile() {
        return macro.scriptFile;
    }
    
    public RawMacro getRawMacro() {
        return macro;
    }
    
    public Thread runMacro(EventTypesEnum type, HashMap<String, Object> args) {
        try {
            return RunScript.exec(macro, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Thread trigger(EventTypesEnum type, HashMap<String, Object> args) {
        return null;
    }
}