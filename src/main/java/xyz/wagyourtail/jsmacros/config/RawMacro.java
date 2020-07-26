package xyz.wagyourtail.jsmacros.config;

import xyz.wagyourtail.jsmacros.macros.MacroEnum;

public class RawMacro {
    public MacroEnum type;
    public String eventkey;
    public String scriptFile;
    public boolean enabled;
    
    public RawMacro(MacroEnum type, String eventkey, String scriptFile, boolean enabled) {
        this.type = type;
        this.eventkey = eventkey;
        this.scriptFile = scriptFile;
    }
    
    public boolean equals(RawMacro macro) {
        return type == macro.type && eventkey.equalsIgnoreCase(macro.eventkey) && scriptFile.equals(macro.scriptFile);
    }
    
    public String toString() {
        return String.format("RawMacro:{\"type\": \"%s\", \"eventkey\": \"%s\", \"scriptFile\": \"%s\", \"enabled\": %b}", type.toString(), eventkey, scriptFile, enabled);
    }
}