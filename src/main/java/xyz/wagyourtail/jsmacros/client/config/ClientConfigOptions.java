package xyz.wagyourtail.jsmacros.client.config;

import xyz.wagyourtail.jsmacros.core.config.ConfigOptions;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.util.Comparator;

public class ClientConfigOptions extends ConfigOptions {
    public SortMethod sortMethod;
    public boolean disableKeyWhenScreenOpen;
    
    public ClientConfigOptions() {
        super();
        this.sortMethod = SortMethod.Enabled;
        this.disableKeyWhenScreenOpen = true;
    }
    
    public Comparator<ScriptTrigger> getSortComparator() {
        if (this.sortMethod == null) this.sortMethod = ClientConfigOptions.SortMethod.Enabled;
        switch(this.sortMethod) {
            default:
            case Enabled:
                return new SortByEnabled();
            case FileName:
                return new SortByFileName();
            case TriggerName:
                return new SortByTriggerName();
        }
    }
    
    public void setSortComparator(ClientConfigOptions.SortMethod method) {
        this.sortMethod = method;
    }
    
    public enum SortMethod {
        Enabled,
        TriggerName,
        FileName
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
    
}
