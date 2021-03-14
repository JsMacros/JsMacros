package xyz.wagyourtail.jsmacros.core.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.impl.EventProfileLoad;

import java.util.*;

public class CoreConfigV2 {
    @Option(translationKey = "Default Profile", group = "profile", options = "profileOptions")
    public String defaultProfile = "default";
    
    @Option(translationKey = "Profiles", group = {"profile", "list"}, type = "profilemap")
    public Map<String, List<ScriptTrigger>> profiles = new HashMap<>();
    
    @Option(translationKey = "Extra Js Options", group = {"languages", "js"}, type = "stringmap")
    public Map<String, String> extraJsOptions = new HashMap<>();
    
    public CoreConfigV2() {
        profiles.put("default", new ArrayList<>());
        profiles.get("default").add(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, EventProfileLoad.class.getAnnotation(Event.class).value(), "index.js", true));
    }
    
    //"synthetic" option
    @Option(translationKey = "Current Profile", group = "general", setter = "setCurrentProfile", options = "profileOptions")
    public String getCurrentProfile() {
        return Core.instance.profile.getCurrentProfileName();
    }
    
    public void setCurrentProfile(String pname) {
        Core.instance.profile.loadOrCreateProfile(pname);
    }
    
    public Set<String> profileOptions() {
        return profiles.keySet();
    }
    
    
    @Deprecated
    public void fromV1(JsonObject v1) {
        defaultProfile = v1.get("defaultProfile").getAsString();
        v1.remove("defaultProfile");
        profiles = new HashMap<>();
        for (Map.Entry<String, JsonElement> el : v1.getAsJsonObject("profiles").entrySet()) {
            List<ScriptTrigger> triggers = new LinkedList<>();
            for (JsonElement el2 : el.getValue().getAsJsonArray()) {
                triggers.add(ConfigManager.gson.fromJson(el2, ScriptTrigger.class));
            }
            profiles.put(el.getKey(), triggers);
        }
        v1.remove("profiles");
        for (Map.Entry<String, JsonElement> el : v1.getAsJsonObject("extraJsOptions").entrySet()) {
            extraJsOptions.put(el.getKey(), el.getValue().getAsString());
        }
        v1.remove("extraJsOptions");
    }
}
