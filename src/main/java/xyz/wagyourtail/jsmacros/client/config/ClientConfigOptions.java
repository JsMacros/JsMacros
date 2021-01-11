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
                return new ScriptTrigger.SortByEnabled();
            case FileName:
                return new ScriptTrigger.SortByFileName();
            case TriggerName:
                return new ScriptTrigger.SortByTriggerName();
        }
    }
    
    public void setSortComparator(ClientConfigOptions.SortMethod method) {
        this.sortMethod = method;
    }
    
    public static enum SortMethod {
        Enabled,
        TriggerName,
        FileName
    }
    
}
