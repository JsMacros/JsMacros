package xyz.wagyourtail.jsmacros.core.language;

import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * Language class for languages to be implemented on top of.
 *
 * @since 1.1.3
 */
public abstract class BaseLanguage<T> {
    public final String extension;
    protected final Core runner;
    
    public BaseLanguage(String extension, Core runner) {
        this.extension = extension;
        this.runner = runner;
    }
    
    public ContextContainer<T> trigger(ScriptTrigger macro, BaseEvent event, Runnable then,
                          Consumer<Throwable> catcher) {
        
        final ScriptTrigger staticMacro = macro.copy();
        final Thread ct = Thread.currentThread();
        ContextContainer<T> ctx = new ContextContainer<>(createContext());
        final Thread t = new Thread(() -> {
            try {
                if (event == null) {
                    Thread.currentThread().setName(String.format("RunScript:{\"creator\":\"%s\"}", ct.getName()));
                } else {
                    Thread.currentThread().setName(String.format("Script:{\"trigger\":\"%s\", \"event\":\"%s\", \"file\":\"%s\"}", staticMacro.triggerType, staticMacro.event, staticMacro.scriptFile));
                }
                File file = new File(runner.config.macroFolder, staticMacro.scriptFile);
                if (file.exists()) {
                    runner.contexts.put(ctx.getCtx(), Thread.currentThread().getName());
                    runner.threadContext.put(Thread.currentThread(), ctx.getCtx());
                    runner.eventContexts.put(Thread.currentThread(), ctx);
                    exec(ctx, staticMacro, file, event);
                    
                    if (then != null) then.run();
                }
            } catch (Exception e) {
                try {
                    if (catcher != null) catcher.accept(e);
                    else throw e;
                } catch (Exception f) {
                    runner.profile.logError(f);
                }
            } finally {
                ContextContainer<?> cc = runner.eventContexts.get(Thread.currentThread());
                if (cc != null) cc.releaseLock();
            }
        });
        ctx.setLockThread(t);
        t.start();
        return ctx;
    }
    
    public ContextContainer<T> trigger(String script, Runnable then, Consumer<Throwable> catcher) {
        final Thread ct = Thread.currentThread();
        ContextContainer<T> ctx = new ContextContainer<>(createContext());
        final Thread t = new Thread(() -> {
            try {
                Thread.currentThread().setName(String.format("RunScript:{\"creator\":\"%s\", \"start\":\"%d\"}", ct.getName(), System.currentTimeMillis()));
                runner.contexts.put(ctx.getCtx(), Thread.currentThread().getName());
                runner.threadContext.put(Thread.currentThread(), ctx.getCtx());
                exec(ctx, script, new HashMap<>(), null);
    
                if (then != null) then.run();
            } catch (Exception e) {
                try {
                    if (catcher != null) catcher.accept(e);
                    else throw e;
                } catch (Exception f) {
                    runner.profile.logError(f);
                }
            } finally {
                ctx.releaseLock();
            }
        });
        ctx.setLockThread(t);
        t.start();
        return ctx;
    }
    
    public Map<String, BaseLibrary> retrieveLibs(ContextContainer<T> context) {
        return runner.libraryRegistry.getLibraries(this, context);
    }
    
    public Map<String, BaseLibrary> retrieveOnceLibs() {
        return runner.libraryRegistry.getOnceLibraries(this);
    }
    
    public Map<String, BaseLibrary> retrievePerExecLibs(ContextContainer<T> context) {
        return runner.libraryRegistry.getPerExecLibraries(this, context);
    }
    
    /**
     * run a script trigger/file with this.
     *
     * @param macro
     * @param file
     * @param event
     *
     * @throws Exception
     * @since 1.2.7 [citation needed]
     */
    protected abstract void exec(ContextContainer<T> ctx, ScriptTrigger macro, File file, BaseEvent event) throws Exception;
    
    /**
     * run a string based script with this.
     *
     * @param script
     * @param globals
     * @param currentDir
     *
     * @throws Exception
     * @since 1.2.7 [citation needed]
     */
    protected abstract void exec(ContextContainer<T> ctx, String script, Map<String, Object> globals, Path currentDir) throws Exception;
    
    /**
     * @param ex
     * @since 1.3.0
     * @return
     */
    public BaseWrappedException<?> wrapException(Throwable ex) {
        return null;
    }
    
    public abstract ScriptContext<T> createContext();
    
    @Deprecated
    public String extension() {
        return extension;
    }
    
}