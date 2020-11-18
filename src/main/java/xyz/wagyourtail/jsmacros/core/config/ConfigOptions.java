package xyz.wagyourtail.jsmacros.core.config;

import java.util.List;
import java.util.Map;

public class ConfigOptions {
    public String defaultProfile;
    public ScriptTrigger.SortMethod sortMethod;
    public boolean disableKeyWhenScreenOpen;
    public Map<String, List<ScriptTrigger>> profiles;
    public Map<String, String> extraJsOptions;
    public boolean lazySyntaxHighlighting;
    
    public ConfigOptions(boolean disableKeyWhenScreenOpen, String defaultProfile, ScriptTrigger.SortMethod sortMethod, Map<String, List<ScriptTrigger>> profiles, Map<String, String> extraJsOptions, boolean lazySyntaxHighlighting) {
        this.defaultProfile = defaultProfile;
        this.profiles = profiles;
        this.disableKeyWhenScreenOpen = disableKeyWhenScreenOpen;
        this.sortMethod = sortMethod;
        this.extraJsOptions = extraJsOptions;
        this.lazySyntaxHighlighting = lazySyntaxHighlighting;
    }
}