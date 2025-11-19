package xyz.wagyourtail.jsmacros.core.config;

import com.google.gson.annotations.SerializedName;

import java.nio.file.Path;

public class ScriptTrigger {
    @SerializedName(value = "triggerType", alternate = "type")
    public TriggerType triggerType;
    @SerializedName(value = "event", alternate = "eventkey")
    public String event;
    public Path scriptFile;
    public boolean enabled;
    public boolean joined;

    public ScriptTrigger(TriggerType triggerType, String event, Path scriptFile, boolean enabled, boolean joined) {
        this(triggerType, event, scriptFile.toString(), enabled, joined);
    }

    @Deprecated
    public ScriptTrigger(TriggerType triggerType, String event, String scriptFile, boolean enabled, boolean joined) {
        this.triggerType = triggerType;
        this.event = event;
        this.scriptFile = Path.of(scriptFile);
        this.enabled = enabled;
        this.joined = joined;
    }

    public boolean equals(ScriptTrigger macro) {
        return triggerType == macro.triggerType && event.equalsIgnoreCase(macro.event) && scriptFile.equals(macro.scriptFile);
    }

    public String toString() {
        return String.format("RawMacro:{\"type\": \"%s\", \"eventkey\": \"%s\", \"scriptFile\": \"%s\", \"enabled\": %b, \"joined\": %b}", triggerType.toString(), event, scriptFile, enabled, joined);
    }

    public static ScriptTrigger copy(ScriptTrigger m) {
        return new ScriptTrigger(m.triggerType, m.event, m.scriptFile, m.enabled, m.joined);
    }

    public ScriptTrigger copy() {
        return copy(this);
    }

    /**
     * @author Wagyourtail
     * @since 1.0.0 [citation needed]
     */
    public enum TriggerType {
        KEY_FALLING,
        KEY_RISING,
        KEY_BOTH,
        EVENT
    }

    /**
     * @return
     * @since 1.2.7
     */
    public TriggerType getTriggerType() {
        return triggerType;
    }

    /**
     * @return
     * @since 1.2.7
     */
    public String getEvent() {
        return event;
    }

    /**
     * @return
     * @since 1.2.7
     */
    public String getScriptFile() {
        return scriptFile.toString();
    }

    /**
     * @since 2.0.0
     * @return
     */
    public Path getScriptPath() {
        return scriptFile;
    }

    /**
     * @return
     * @since 1.2.7
     */
    public boolean getEnabled() {
        return enabled;
    }

}
