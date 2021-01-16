package xyz.wagyourtail.jsmacros.core.config;

import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.impl.EventProfileLoad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigOptions {
    public String defaultProfile = "default";
    public Map<String, List<ScriptTrigger>> profiles = new HashMap<>();
    public Map<String, String> extraJsOptions = new HashMap<>();
    
    public ConfigOptions() {
        profiles.put("default", new ArrayList<>());
        profiles.get("default").add(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, EventProfileLoad.class.getAnnotation(Event.class).value(), "index.js", true));
    }
}