package xyz.wagyourtail.jsmacros.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigOptions {
    public String defaultProfile = "default";
    public Map<String, List<RawMacro>> profiles = new HashMap<>();
    public boolean disableKeyWhenScreenOpen = true;
    
    public ConfigOptions(boolean disableKeyWhenScreenOpen, String defaultProfile, Map<String, List<RawMacro>> profiles) {
        this.defaultProfile = defaultProfile;
        this.profiles = profiles;
        this.disableKeyWhenScreenOpen = disableKeyWhenScreenOpen;
    }
}