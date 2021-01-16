package xyz.wagyourtail.jsmacros.client.config;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ConfigOptions;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ClientConfigOptions extends ConfigOptions {
    public SortMethod sortMethod = SortMethod.Enabled;
    public boolean disableKeyWhenScreenOpen = true;
    public Map<String, short[]> editorTheme = null;
    public Map<String, String> linterOverrides = null;
    public int editorHistorySize = 20;
    public boolean editorSuggestions = true;
    
    public ClientConfigOptions() {
        super();
    }
    
    public Map<String, String> getLinterOverrides() {
        if (linterOverrides == null) {
            linterOverrides = new HashMap<>();
            Core.instance.config.saveConfig();
        }
        return linterOverrides;
    }
    
    public Map<String, short[]> getThemeData() {
        if (editorTheme == null) {
            editorTheme = new HashMap<>();
            // JS
            editorTheme.put("keyword", new short[] {0xCC, 0x78, 0x32});
            editorTheme.put("number",  new short[] {0x79, 0xAB, 0xFF});
            editorTheme.put("function-variable", new short[] {0x79, 0xAB, 0xFF});
            editorTheme.put("function", new short[] {0xA2, 0xEA, 0x22});
            editorTheme.put("operator", new short[] {0xD8, 0xD8, 0xD8});
            editorTheme.put("string", new short[] {0x12, 0xD4, 0x89});
            editorTheme.put("comment", new short[] {0xA0, 0xA0, 0xA0});
            editorTheme.put("constant", new short[] {0x21, 0xB4, 0x3E});
            editorTheme.put("class-name", new short[] {0x21, 0xB4, 0x3E});
            editorTheme.put("boolean", new short[] {0xFF, 0xE2, 0x00});
            editorTheme.put("punctuation", new short[] {0xD8, 0xD8, 0xD8});
            editorTheme.put("interpolation-punctuation", new short[] {0xCC, 0x78, 0x32});
    
            //py
            editorTheme.put("builtin", new short[] {0x21, 0xB4, 0x3E});
            editorTheme.put("format-spec", new short[] {0xCC, 0x78, 0x32});
    
            //regex
            editorTheme.put("regex", new short[] {0x12, 0xD4, 0x89});
            editorTheme.put("charset-negation", new short[] {0xCC, 0x78, 0x32});
            editorTheme.put("charset-punctuation", new short[] {0xD8, 0xD8, 0xD8});
            editorTheme.put("escape", new short[]  {0xFF, 0xE2, 0x00});
            editorTheme.put("charclass", new short[] {0xFF, 0xE2, 0x00});
            editorTheme.put("quantifier", new short[] {0x79, 0xAB, 0xFF});
            Core.instance.config.saveConfig();
        }
        return editorTheme;
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
