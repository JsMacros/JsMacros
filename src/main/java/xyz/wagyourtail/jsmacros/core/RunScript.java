package xyz.wagyourtail.jsmacros.core;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.jsmacros.core.event.IEventTrigger;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventRegistry;
import xyz.wagyourtail.jsmacros.core.library.LibraryRegistry;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class RunScript {
    public static final EventRegistry eventRegistry = new EventRegistry();
    public static final LibraryRegistry libraryRegistry = new LibraryRegistry();
    public static final Map<IEventTrigger, List<IScriptThreadWrapper>> threads = new HashMap<>();
    public static final List<ILanguage> languages = new ArrayList<>();
    public static ILanguage defaultLang = new JavascriptLanguageDefinition();
    
    static {
        addLanguage(defaultLang);
    }

    public static void addLanguage(ILanguage l) {
        languages.add(l);
    }
    
    public static void sortLanguages() {
        Collections.sort(languages, new sortLanguage());
        
    }
    
    public static List<IScriptThreadWrapper> getThreads() {
        List<IScriptThreadWrapper> th = new ArrayList<>();
        for (List<IScriptThreadWrapper> tl : ImmutableList.copyOf(threads.values())) {
            th.addAll(tl);
        }
        return th;
    }

    public static void removeThread(ScriptThreadWrapper t) {
        if (threads.containsKey(t.m)) {
            if (threads.get(t.m).remove(t)) return;
        }
        for (Entry<IEventTrigger, List<IScriptThreadWrapper>> tl : threads.entrySet()) {
            if (tl.getValue().remove(t)) return;
        }
    }

    public static Thread exec(ScriptTrigger macro, BaseEvent event) {
        return exec(macro, event, null, null);
    }

    public static Thread exec(ScriptTrigger macro, BaseEvent event, Runnable then,
                              Consumer<Throwable> catcher) {
        for (ILanguage language : languages) {
            if (macro.scriptFile.endsWith(language.extension()))
                return language.trigger(macro, event, then, catcher);
        }
        return defaultLang.trigger(macro, event, then, catcher);
    }

    public static class ScriptThreadWrapper implements IScriptThreadWrapper {
        public final Thread t;
        public final ScriptTrigger m;
        public final long startTime;

        public ScriptThreadWrapper(Thread t, ScriptTrigger m, long startTime) {
            this.t = t;
            this.m = m;
            this.startTime = startTime;
        }

        public void start() {
            t.start();
        }

        @Override
        public Thread getThread() {
            return t;
        }

        @Override
        public IEventTrigger getRawMacro() {
            return m;
        }

        @Override
        public long getStartTime() {
            return startTime;
        }
    }
    
    public static class sortLanguage implements Comparator<ILanguage> {

        @Override
        public int compare(ILanguage a, ILanguage b) {
            final String[] as = a.extension().replaceAll("\\.", " ").trim().split(" ");
            final String[] bs = b.extension().replaceAll("\\.", " ").trim().split(" ");
            final int lendif = bs.length-as.length;
            if (lendif != 0) return lendif;
            int comp = 0;
            for (int i = bs.length - 1; i >= 0; --i) {
                comp = as[i].compareTo(bs[i]);
                if (comp != 0) break;
            }
            return comp;
        }
        
    }
}
