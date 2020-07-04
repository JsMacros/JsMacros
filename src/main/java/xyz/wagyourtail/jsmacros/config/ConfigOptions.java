package xyz.wagyourtail.jsmacros.config;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigOptions {
    public String defaultProfile;
    public HashMap<String, ArrayList<RawMacro>> profiles;
    public boolean disableKeyWhenScreenOpen;
    
    public ConfigOptions(boolean disableKeyWhenScreenOpen, String defaultProfile, HashMap<String, ArrayList<RawMacro>> profiles) {
        this.defaultProfile = defaultProfile;
        this.profiles = profiles;
        this.disableKeyWhenScreenOpen = disableKeyWhenScreenOpen;
    }
}