package xyz.wagyourtail.jsmacros.core;

import org.slf4j.Logger;
import xyz.wagyourtail.SynchronizedWeakHashSet;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;
import xyz.wagyourtail.jsmacros.core.extensions.Extension;
import xyz.wagyourtail.jsmacros.core.extensions.ExtensionLoader;
import xyz.wagyourtail.jsmacros.core.extensions.LanguageExtension;
import xyz.wagyourtail.jsmacros.core.helper.ClassWrapperTree;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.LibraryRegistry;
import xyz.wagyourtail.jsmacros.core.service.ServiceManager;
import xyz.wagyourtail.jsmacros.core.threads.JsMacrosThreadPool;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Core<T extends BaseProfile, U extends BaseEventRegistry> {
    private final Set<BaseScriptContext<?>> contexts = new SynchronizedWeakHashSet<>();

    public final ClassWrapperTree<Object, BaseHelper<?>> helperRegistry = new ClassWrapperTree<>(Object.class, a -> null);
    public final LibraryRegistry libraryRegistry = new LibraryRegistry(this);
    public final BaseEventRegistry eventRegistry;

    public final ExtensionLoader extensions;

    public final T profile;
    public final ConfigManager config;
    public final ServiceManager services;

    public final JsMacrosThreadPool threadPool = new JsMacrosThreadPool();

    public Core(Function<Core<T, U>, U> eventRegistryFunction, BiFunction<Core<T, U>, Logger, T> profileFunction, File configFolder, File macroFolder, Logger logger) {
        eventRegistry = eventRegistryFunction.apply(this);
        config = new ConfigManager(this, configFolder, macroFolder, logger);
        profile = profileFunction.apply(this, logger);

        extensions = new ExtensionLoader(this);
        this.services = new ServiceManager(this);
        profile.init(config.getOptions(CoreConfigV2.class).defaultProfile);
        services.load();
    }

    /**
     * @param container
     */
    public void addContext(EventContainer<?> container) {
        contexts.add(container.getCtx());
    }

    /**
     * @return
     */
    public Set<BaseScriptContext<?>> getContexts() {
        return contexts;
    }

    /**
     * executes an {@link BaseEvent Event} on a ${@link ScriptTrigger}
     *
     * @param macro
     * @param event
     * @return
     */
    public EventContainer<?> exec(ScriptTrigger macro, BaseEvent event) {
        return exec(macro, event, null, null);
    }

    /**
     * Executes an {@link BaseEvent Event} on a ${@link ScriptTrigger} with callback.
     *
     * @param macro
     * @param event
     * @param then
     * @param catcher
     * @return
     */
    public EventContainer<?> exec(ScriptTrigger macro, BaseEvent event, Runnable then,
                                  Consumer<Throwable> catcher) {

        final File file = new File(this.config.macroFolder, macro.scriptFile);
        LanguageExtension l = extensions.getExtensionForFile(file);
        if (l == null) {
            l = extensions.getHighestPriorityExtension();
        }
        return l.getLanguage(this).trigger(macro, event, then, catcher);
    }

    /**
     * @param lang
     * @param script
     * @param fakeFile
     * @param event
     * @param then
     * @param catcher
     * @return
     * @since 1.7.0
     */
    public EventContainer<?> exec(String lang, String script, File fakeFile, BaseEvent event, Runnable then, Consumer<Throwable> catcher) {
        LanguageExtension l = extensions.getExtensionForFile(fakeFile != null ? fakeFile : new File(lang.startsWith(".") ? lang : "." + lang));
        assert l != null;
        return l.getLanguage(this).trigger(lang, script, fakeFile, event, then, catcher);
    }

    /**
     * wraps an exception for more uniform parsing between languages, also extracts useful info.
     *
     * @param ex exception to wrap.
     * @return
     */
    public BaseWrappedException<?> wrapException(Throwable ex) {
        if (ex == null) {
            return null;
        }
        for (LanguageExtension lang : extensions.getAllLanguageExtensions()) {
            BaseWrappedException<?> e = lang.wrapException(ex);
            if (e != null) {
                return e;
            }
        }
        Iterator<StackTraceElement> elements = Arrays.stream(ex.getStackTrace()).iterator();
        String message = ex.getClass().getSimpleName();
        String intMessage = ex.getMessage();
        if (intMessage != null) {
            message += ": " + intMessage;
        }
        return new BaseWrappedException<>(ex, message, null, elements.hasNext() ? wrapHostInternal(elements.next(), elements) : null);
    }

    private BaseWrappedException<StackTraceElement> wrapHostInternal(StackTraceElement e, Iterator<StackTraceElement> elements) {
        return BaseWrappedException.wrapHostElement(e, elements.hasNext() ? wrapHostInternal(elements.next(), elements) : null);
    }

}
