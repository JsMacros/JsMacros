package xyz.wagyourtail.jsmacros.core.language;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.extensions.Extension;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.service.EventService;

import java.io.File;
import java.nio.file.FileSystemException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Language class for languages to be implemented on top of.
 *
 * @since 1.1.3
 */
public abstract class BaseLanguage<U, T extends BaseScriptContext<U>> {
    protected final Core<?, ?> runner;
    public final Extension extension;

    public Runnable preThread = () -> {};
    
    public BaseLanguage(Extension extension, Core<?, ?> runner) {
        this.runner = runner;
        this.extension = extension;
    }
    
    public final EventContainer<T> trigger(ScriptTrigger macro, BaseEvent event, Runnable then,
                                     Consumer<Throwable> catcher) {
        
        final ScriptTrigger staticMacro = macro.copy();
        final Thread ct = Thread.currentThread();
        final File file = new File(runner.config.macroFolder, staticMacro.scriptFile);
        EventContainer<T> ctx = new EventContainer<>(createContext(event, file));
        final Thread t = new Thread(() -> {
            preThread.run();
            try {
                if (event == null) {
                    Thread.currentThread().setName(String.format("RunScript:{\"creator\":\"%s\"}", ct.getName()));
                } else {
                    Thread.currentThread().setName(String.format("Script:{\"trigger\":\"%s\", \"event\":\"%s\", \"file\":\"%s\"}", staticMacro.triggerType, staticMacro.event, staticMacro.scriptFile));
                }
                if (file.exists() && file.isFile()) {

                    runner.addContext(ctx);

                    exec(ctx, staticMacro, event);
                    try {
                        if (then != null)
                            then.run();
                    } catch (Throwable e) {
                        runner.profile.logError(e);
                    }
                } else {
                    macro.enabled = false;
                    if (staticMacro.scriptFile.isEmpty()) {
                        throw new RuntimeException("No script file was selected for trigger" + staticMacro.event + "!");
                    } else {
                        throw new FileSystemException("file \"" + file.getPath() + "\" does not exist or is a directory!");
                    }
                }
            } catch (Throwable e) {
                try {
                    if (catcher != null) catcher.accept(e);
                    else throw e;
                } catch (Throwable f) {
                    runner.profile.logError(f);
                }
            } finally {
                ctx.getCtx().unbindThread(Thread.currentThread());

                if (event instanceof EventService) {
                    runner.services.markCrashed(((EventService) event).serviceName);
                }
                
                EventContainer<?> cc = ctx.getCtx().events.get(Thread.currentThread());
                if (cc != null) cc.releaseLock();

                ctx.getCtx().clearSyncObject();
                if (!ctx.getCtx().hasMethodWrapperBeenInvoked) {
                    ctx.getCtx().closeContext();
                }
            }
        });
        ctx.setLockThread(t);
        ctx.getCtx().setMainThread(t);
        t.start();
        return ctx;
    }
    
    public final EventContainer<T> trigger(String lang, String script, File fakeFile, BaseEvent event, Runnable then, Consumer<Throwable> catcher) {
        final Thread ct = Thread.currentThread();
        EventContainer<T> ctx = new EventContainer<>(createContext(event, fakeFile));
        final Thread t = new Thread(() -> {
            preThread.run();
            try {
                Thread.currentThread().setName(String.format("RunScript:{\"creator\":\"%s\", \"start\":\"%d\"}", ct.getName(), System.currentTimeMillis()));

                runner.addContext(ctx);

                exec(ctx, lang, script, event);

                if (then != null) then.run();
            } catch (Throwable e) {
                try {
                    if (catcher != null) catcher.accept(e);
                    else throw e;
                } catch (Throwable f) {
                    runner.profile.logError(f);
                }
            } finally {
                ctx.getCtx().unbindThread(Thread.currentThread());

                EventContainer<?> cc = ctx.getCtx().events.get(Thread.currentThread());
                if (cc != null) cc.releaseLock();

                ctx.getCtx().clearSyncObject();
                if (!ctx.getCtx().hasMethodWrapperBeenInvoked) {
                    ctx.getCtx().closeContext();
                }
            }
        });
        ctx.setLockThread(t);
        ctx.getCtx().setMainThread(t);
        t.start();
        return ctx;
    }
    
    public Map<String, BaseLibrary> retrieveLibs(T context) {
        return runner.libraryRegistry.getLibraries(this, context);
    }
    
    public Map<String, BaseLibrary> retrieveOnceLibs() {
        return runner.libraryRegistry.getOnceLibraries(this);
    }
    
    public Map<String, BaseLibrary> retrievePerExecLibs(T context) {
        return runner.libraryRegistry.getPerExecLibraries(this, context);
    }
    
    /**
     * run a script trigger/file with this.
     *
     * @param macro
     * @param event
     *
     * @throws Exception
     * @since 1.2.7 [citation needed]
     */
    protected abstract void exec(EventContainer<T> ctx, ScriptTrigger macro, BaseEvent event) throws Exception;
    
    /**
     * run a string based script with this.
     *
     *
     * @param ctx
     * @param script
     * @param event
     * @throws Exception
     * @since 1.7.0
     */
    protected abstract void exec(EventContainer<T> ctx, String lang, String script, BaseEvent event) throws Exception;
    
    public abstract T createContext(BaseEvent event, File file);
    
}