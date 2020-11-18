package xyz.wagyourtail.jsmacros.core.config;

import xyz.wagyourtail.jsmacros.core.event.IEventTrigger;

import java.util.Comparator;

public class ScriptTrigger implements IEventTrigger {
    public TriggerType triggerType;
    public String event;
    public String scriptFile;
    public boolean enabled;
    
    public ScriptTrigger(TriggerType triggerType, String event, String scriptFile, boolean enabled) {
        this.triggerType = triggerType;
        this.event = event;
        this.scriptFile = scriptFile;
    }
    
    public boolean equals(ScriptTrigger macro) {
        return triggerType == macro.triggerType && event.equalsIgnoreCase(macro.event) && scriptFile.equals(macro.scriptFile);
    }
    
    public String toString() {
        return String.format("RawMacro:{\"type\": \"%s\", \"eventkey\": \"%s\", \"scriptFile\": \"%s\", \"enabled\": %b}", triggerType.toString(), event, scriptFile, enabled);
    }
    
    public static IEventTrigger copy(ScriptTrigger m) {
        return new ScriptTrigger(m.triggerType, m.event, m.scriptFile, m.enabled);
    }
    
    public IEventTrigger copy() {
        return copy(this);
    }
    
    public static class SortByEnabled implements Comparator<ScriptTrigger> {
        @Override
        public int compare(ScriptTrigger a, ScriptTrigger b) {
            if (a.enabled ^ b.enabled) {
                return a.enabled ? -1 : 1;
            } else {
                return a.toString().compareTo(b.toString());
            }
        }
    }
    public static class SortByTriggerName implements Comparator<ScriptTrigger> {
        @Override
        public int compare(ScriptTrigger a, ScriptTrigger b) {
            int comp = a.event.compareTo(b.event);
            if (comp != 0) return comp;
            if (a.enabled ^ b.enabled) return a.enabled ? -1 : 1;
            return a.toString().compareTo(b.toString());
        }
    }
    public static class SortByFileName implements Comparator<ScriptTrigger> {
        @Override
        public int compare(ScriptTrigger a, ScriptTrigger b) {
            int comp = a.scriptFile.compareTo(b.scriptFile);
            if (comp != 0) return comp;
            if (a.enabled ^ b.enabled) return a.enabled ? -1 : 1;
            return a.toString().compareTo(b.toString());
        }
    }
    
    public static enum SortMethod {
        Enabled,
        TriggerName,
        FileName
    }

    @Override
    public TriggerType getTriggerType() {
        return triggerType;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getScriptFile() {
        return scriptFile;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }
}