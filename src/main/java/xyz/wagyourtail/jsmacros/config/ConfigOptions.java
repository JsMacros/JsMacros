package xyz.wagyourtail.jsmacros.config;

import java.util.List;
import java.util.Map;

public class ConfigOptions {
    public String defaultProfile;
    public RawMacro.SortMethod sortMethod;
    public boolean disableKeyWhenScreenOpen;
    public Map<String, List<RawMacro>> profiles;
    public Map<String, String> extraJsOptions;
    public boolean lazySyntaxHighlighting;
    
    public ConfigOptions(boolean disableKeyWhenScreenOpen, String defaultProfile, RawMacro.SortMethod sortMethod, Map<String, List<RawMacro>> profiles, Map<String, String> extraJsOptions, boolean lazySyntaxHighlighting) {
        this.defaultProfile = defaultProfile;
        this.profiles = profiles;
        this.disableKeyWhenScreenOpen = disableKeyWhenScreenOpen;
        this.sortMethod = sortMethod;
        this.extraJsOptions = extraJsOptions;
        this.lazySyntaxHighlighting = lazySyntaxHighlighting;
    }
}