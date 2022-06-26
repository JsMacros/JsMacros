package xyz.wagyourtail.jsmacros.js;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import xyz.wagyourtail.jsmacros.core.config.Option;
import xyz.wagyourtail.jsmacros.core.config.OptionType;

import java.util.HashMap;
import java.util.Map;

public class JSConfig {

    @Option(translationKey = "jsmacros.settings.languages.extragraaloptions", group = {"jsmacros.settings.languages", "jsmacros.settings.languages.graaloptions"}, type = @OptionType("string"))
    public Map<String, String> extraGraalOptions = new HashMap<>();

    @Deprecated
    public void fromV1(JsonObject v1) {
        for (Map.Entry<String, JsonElement> el : v1.getAsJsonObject("extraJsOptions").entrySet()) {
            extraGraalOptions.put(el.getKey(), el.getValue().getAsString());
        }
        v1.remove("extraJsOptions");
    }

    @Deprecated
    public void fromV2(JsonObject v2) {
        JsonElement v1 = v2.get("client");
        if (v1 != null && v1.isJsonObject()) {
            fromV1(v1.getAsJsonObject());
        }
    }
}
