package xyz.wagyourtail.jsmacros.client.config;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;

import java.util.Comparator;

public class Sorting {
    public enum MacroSortMethod {
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

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public enum ServiceSortMethod {
        Name,
        FileName,
        Enabled,
        Running;
    }
    
    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class SortServiceByEnabled implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            boolean aEnabled = Core.getInstance().services.isEnabled(a);
            boolean bEnabled = Core.getInstance().services.isEnabled(b);
            if (aEnabled ^ bEnabled) {
                return aEnabled ? -1 : 1;
            } else {
                return a.compareTo(b);
            }
        }
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class SortServiceByName implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            return a.compareTo(b);
        }
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class SortServiceByRunning implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            boolean aRunning = Core.getInstance().services.isRunning(a);
            boolean bRunning = Core.getInstance().services.isRunning(b);
            if (aRunning ^ bRunning) {
                return aRunning ? -1 : 1;
            } else {
                return a.compareTo(b);
            }
        }
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class SortServiceByFileName implements Comparator<String> {
        @Override
        public int compare(String a, String b) {
            String fileA = Core.getInstance().services.getTrigger(a).file;
            String fileB = Core.getInstance().services.getTrigger(b).file;
            int comp = fileA.compareTo(fileB);
            return comp != 0 ? comp : new SortServiceByEnabled().compare(a, b);
        }
    }
    
}