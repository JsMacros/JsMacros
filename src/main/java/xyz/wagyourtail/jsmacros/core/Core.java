package xyz.wagyourtail.jsmacros.core;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.Logger;
import xyz.wagyourtail.jsmacros.core.config.*;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.language.impl.JavascriptLanguageDefinition;
import xyz.wagyourtail.jsmacros.core.library.LibraryRegistry;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Core {
    public static Core instance;
    
    public final BaseEventRegistry eventRegistry;
    public final BaseProfile profile;
    public final ConfigManager config;
    public final LibraryRegistry libraryRegistry = new LibraryRegistry();
    public final Map<ScriptTrigger, Set<ScriptThreadWrapper>> threads = new HashMap<>();
    public final List<BaseLanguage> languages = new ArrayList<>();
    public BaseLanguage defaultLang = new JavascriptLanguageDefinition(".js", this);
    
    protected Core(Function<Core, BaseEventRegistry> eventRegistryFunction, Function<Core, BaseProfile> profileFunction, File configFolder, File macroFolder, Logger logger) {
        eventRegistry = eventRegistryFunction.apply(this);
        profile = profileFunction.apply(this);
        config = new ConfigManager(configFolder, macroFolder, logger);
        addLanguage(defaultLang);
    }
    
    /**
    * start by running this function, supplying implementations of {@link BaseEventRegistry} and {@link BaseProfile} and a {@link Supplier} for
    * creating the config manager with the folder paths it needs.
    *
     * @param eventRegistryFunction
     * @param profileFunction
     *
     * @param configFolder
     * @param macroFolder
     * @param logger
     * @return
     */
    public static Core createInstance(Function<Core, BaseEventRegistry> eventRegistryFunction, Function<Core, BaseProfile> profileFunction, File configFolder, File macroFolder, Logger logger) {
        if (instance != null) throw new RuntimeException("Can't declare RunScript instance more than once");
        instance = new Core(eventRegistryFunction, profileFunction, configFolder, macroFolder, logger);
        instance.profile.init(instance.config.getOptions(CoreConfigV2.class).defaultProfile);
        return instance;
    }

    public void addLanguage(BaseLanguage l) {
        languages.add(l);
    }
    
    public void sortLanguages() {
        languages.sort(new sortLanguage());
        
    }
    
    public List<ScriptThreadWrapper> getThreads() {
        List<ScriptThreadWrapper> th = new ArrayList<>();
        for (Set<ScriptThreadWrapper> tl : ImmutableList.copyOf(threads.values())) {
            th.addAll(tl);
        }
        return th;
    }

    public void removeThread(ScriptThreadWrapper t) {
        if (threads.containsKey(t.m)) {
            if (threads.get(t.m).remove(t)) return;
        }
        for (Entry<ScriptTrigger, Set<ScriptThreadWrapper>> tl : threads.entrySet()) {
            if (tl.getValue().remove(t)) return;
        }
    }

    public Thread exec(ScriptTrigger macro, BaseEvent event) {
        return exec(macro, event, null, null);
    }

    public Thread exec(ScriptTrigger macro, BaseEvent event, Runnable then,
                              Consumer<Throwable> catcher) {
        for (BaseLanguage language : languages) {
            if (macro.scriptFile.endsWith(language.extension))
                return language.trigger(macro, event, then, catcher);
        }
        return defaultLang.trigger(macro, event, then, catcher);
    }
    
    public BaseWrappedException<?> wrapException(Throwable ex) {
        if (ex == null) return null;
        for (BaseLanguage lang : languages) {
            BaseWrappedException<?> e = lang.wrapException(ex);
            if (e != null) return e;
        }
        Iterator<StackTraceElement> elements = Arrays.stream(ex.getStackTrace()).iterator();
        String message = ex.getClass().getName();
        String intMessage = ex.getMessage();
        if (intMessage != null) {
            message += ": " + intMessage;
        }
        return new BaseWrappedException<>(ex, message, null, elements.hasNext() ? wrapHostInternal(elements.next(), elements) : null);
    }
    
    private BaseWrappedException<StackTraceElement> wrapHostInternal(StackTraceElement e, Iterator<StackTraceElement> elements) {
        return BaseWrappedException.wrapHostElement(e, elements.hasNext() ? wrapHostInternal(elements.next(), elements) : null);
    }
    
    public static class sortLanguage implements Comparator<BaseLanguage> {

        @Override
        public int compare(BaseLanguage a, BaseLanguage b) {
            final String[] as = a.extension.replaceAll("\\.", " ").trim().split(" ");
            final String[] bs = b.extension.replaceAll("\\.", " ").trim().split(" ");
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
