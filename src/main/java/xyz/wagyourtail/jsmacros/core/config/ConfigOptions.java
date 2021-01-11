package xyz.wagyourtail.jsmacros.core.config;

import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.impl.EventProfileLoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigOptions {
    public String defaultProfile;
    public Map<String, List<ScriptTrigger>> profiles;
    public Map<String, String> extraJsOptions;
    
    public ConfigOptions() {
        this.defaultProfile = "default";
        this.profiles = new HashMap<>();
        this.extraJsOptions = new HashMap<>();
        profiles.put("default", new ArrayList<>());
        profiles.get("default").add(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, EventProfileLoad.class.getAnnotation(Event.class).value(), "index.js", true));
    
    }
}