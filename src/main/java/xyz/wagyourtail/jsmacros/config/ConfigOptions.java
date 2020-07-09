package xyz.wagyourtail.jsmacros.config;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigOptions {
    public String defaultProfile = "default";
    public String JEPSharedLibraryPath = "./jep.dll";
    public HashMap<String, ArrayList<RawMacro>> profiles = new HashMap<>();
    public boolean disableKeyWhenScreenOpen = true;
    public boolean enableJEP = false;
    
    public ConfigOptions(boolean disableKeyWhenScreenOpen, boolean enableJEP, String JEPSharedLibraryPath, String defaultProfile, HashMap<String, ArrayList<RawMacro>> profiles) {
        this.defaultProfile = defaultProfile;
        this.profiles = profiles;
        this.disableKeyWhenScreenOpen = disableKeyWhenScreenOpen;
        this.JEPSharedLibraryPath = JEPSharedLibraryPath;
        this.enableJEP = enableJEP;
    }
}