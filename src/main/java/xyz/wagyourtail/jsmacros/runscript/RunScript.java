package xyz.wagyourtail.jsmacros.runscript;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.jsmacros.api.functions.*;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IRawMacro;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IScriptThreadWrapper;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.extensionbase.Functions;
import xyz.wagyourtail.jsmacros.extensionbase.ILanguage;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class RunScript {
    public static Map<IRawMacro, List<IScriptThreadWrapper>> threads = new HashMap<>();
    public static List<ILanguage> languages = new ArrayList<>();
    public static ILanguage defaultLang = new JavascriptLanguageDefinition();
    public static List<Functions> standardLib = new ArrayList<>();
    
    static {
        standardLib.add(new FGlobalVars("globalvars"));
        standardLib.add(new FJsMacros("jsmacros"));
        standardLib.add(new FTime("time"));
        standardLib.add(new FKeyBind("keybind"));
        standardLib.add(new FChat("chat"));
        standardLib.add(new FWorld("world"));
        standardLib.add(new FPlayer("player"));
        standardLib.add(new FHud("hud"));
        standardLib.add(new FRequest("request"));
        standardLib.add(new FFS("fs"));
        standardLib.add(new FReflection("reflection"));

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
        for (Entry<IRawMacro, List<IScriptThreadWrapper>> tl : threads.entrySet()) {
            if (tl.getValue().remove(t)) return;
        }
    }

    public static Thread exec(RawMacro macro, IEvent event) {
        return exec(macro, event, null, null);
    }

    public static Thread exec(RawMacro macro, IEvent event, Runnable then,
        Consumer<String> catcher) {
        for (ILanguage language : languages) {
            if (macro.scriptFile.endsWith(language.extension()))
                return language.trigger(macro, event, then, catcher);
        }
        return defaultLang.trigger(macro, event, then, catcher);
    }

    public static class ScriptThreadWrapper implements IScriptThreadWrapper {
        public final Thread t;
        public final RawMacro m;
        public final long startTime;

        public ScriptThreadWrapper(Thread t, RawMacro m, long startTime) {
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
        public IRawMacro getRawMacro() {
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
