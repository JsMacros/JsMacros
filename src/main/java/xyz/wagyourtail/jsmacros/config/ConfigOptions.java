package xyz.wagyourtail.jsmacros.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigOptions {
    public String defaultProfile = "default";
    public String JEPSharedLibraryPath = "./jep.dll";
    public Map<String, List<RawMacro>> profiles = new HashMap<>();
    public boolean disableKeyWhenScreenOpen = true;
    public boolean enableJEP = false;
    
    public ConfigOptions(boolean disableKeyWhenScreenOpen, boolean enableJEP, String JEPSharedLibraryPath, String defaultProfile, Map<String, List<RawMacro>> profiles) {
        this.defaultProfile = defaultProfile;
        this.profiles = profiles;
        this.disableKeyWhenScreenOpen = disableKeyWhenScreenOpen;
        this.JEPSharedLibraryPath = JEPSharedLibraryPath;
        this.enableJEP = enableJEP;
    }
}