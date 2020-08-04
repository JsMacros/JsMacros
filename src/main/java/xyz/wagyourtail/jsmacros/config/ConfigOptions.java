package xyz.wagyourtail.jsmacros.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigOptions {
    public String defaultProfile = "default";
    public RawMacro.SortMethod sortMethod = RawMacro.SortMethod.Enabled;
    public boolean disableKeyWhenScreenOpen = true;
    public Map<String, List<RawMacro>> profiles = new HashMap<>();
    
    public ConfigOptions(boolean disableKeyWhenScreenOpen, String defaultProfile, RawMacro.SortMethod sortMethod, Map<String, List<RawMacro>> profiles) {
        this.defaultProfile = defaultProfile;
        this.profiles = profiles;
        this.disableKeyWhenScreenOpen = disableKeyWhenScreenOpen;
        this.sortMethod = sortMethod;
    }
}