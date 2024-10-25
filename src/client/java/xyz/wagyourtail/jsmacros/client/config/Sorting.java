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
            if (comp != 0) {
                return comp;
            }
            if (a.enabled ^ b.enabled) {
                return a.enabled ? -1 : 1;
            }
            return a.toString().compareTo(b.toString());
        }

    }

    public static class SortByFileName implements Comparator<ScriptTrigger> {
        @Override
        public int compare(ScriptTrigger a, ScriptTrigger b) {
            int comp = a.scriptFile.compareTo(b.scriptFile);
            if (comp != 0) {
                return comp;
            }
            if (a.enabled ^ b.enabled) {
                return a.enabled ? -1 : 1;
            }
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
        private final Core<?, ?> runner;

        public SortServiceByEnabled(Core<?, ?> runner) {
            this.runner = runner;
        }

        @Override
        public int compare(String a, String b) {
            boolean aEnabled = runner.services.isEnabled(a);
            boolean bEnabled = runner.services.isEnabled(b);
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
        private final Core<?, ?> runner;

        public SortServiceByRunning(Core<?, ?> runner) {
            this.runner = runner;
        }

        @Override
        public int compare(String a, String b) {
            boolean aRunning = runner.services.isRunning(a);
            boolean bRunning = runner.services.isRunning(b);
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
        private final Core<?, ?> runner;

        public SortServiceByFileName(Core<?, ?> runner) {
            this.runner = runner;
        }

        @Override
        public int compare(String a, String b) {
            String fileA = runner.services.getTrigger(a).file.getAbsolutePath();
            String fileB = runner.services.getTrigger(b).file.getAbsolutePath();
            int comp = fileA.compareTo(fileB);
            return comp != 0 ? comp : a.compareTo(b);
        }

    }

}
