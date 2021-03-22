package xyz.wagyourtail.jsmacros.core.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.impl.EventProfileLoad;

import java.util.*;

public class CoreConfigV2 {
    @Option(translationKey = "jsmacros.defaultprofile", group = "jsmacros.settings.profile", options = "profileOptions")
    public String defaultProfile = "default";
    
    @Option(translationKey = "jsmacros.profiles", group = {"jsmacros.settings.profile", "jsmacros.settings.profile.list"}, type = @OptionType("profile"))
    public Map<String, List<ScriptTrigger>> profiles = new HashMap<>();
    
    @Option(translationKey = "jsmacros.extrajsoptions", group = {"jsmacros.settings.languages", "jsmacros.settings.languages.javascript", "jsmacros.settings.languages.javascript.graaloptions"}, type = @OptionType("string"))
    public Map<String, String> extraJsOptions = new HashMap<>();
    
    public CoreConfigV2() {
        profiles.put("default", new ArrayList<>());
        profiles.get("default").add(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, EventProfileLoad.class.getAnnotation(Event.class).value(), "index.js", true));
    }
    
    //"synthetic" option
    @Option(translationKey = "jsmacros.currentprofile", group = "jsmacros.settings.profile", setter = "setCurrentProfile", options = "profileOptions")
    public String getCurrentProfile() {
        return Core.instance.profile.getCurrentProfileName();
    }
    
    public void setCurrentProfile(String pname) {
        Core.instance.profile.loadOrCreateProfile(pname);
    }
    
    public List<String> profileOptions() {
        return new ArrayList<>(profiles.keySet());
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
