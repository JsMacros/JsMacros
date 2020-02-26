package xyz.wagyourtail.jsmacros.config;

import java.io.File;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;

public class RawMacro {
    public MacroEnum type;
    public String eventkey;
    public File scriptFile;
    
    public RawMacro(MacroEnum type, String eventkey, File scriptFile) {
        this.type = type;
        this.eventkey = eventkey;
        this.scriptFile = scriptFile;
    }
    
    public String scriptFileName() {
        return scriptFile != null ? scriptFile.toString().substring(jsMacros.config.macroFolder.toString().length() + 1) : "";
    }
    
    public boolean equals(RawMacro macro) {
        return type == macro.type && eventkey.equalsIgnoreCase(macro.eventkey) && scriptFile.equals(macro.scriptFile);
    }
}