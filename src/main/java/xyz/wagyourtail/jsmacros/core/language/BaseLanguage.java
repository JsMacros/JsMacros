package xyz.wagyourtail.jsmacros.core.language;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptThreadWrapper;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Language class for languages to be implemented on top of.
 *
 * @since 1.1.3
 */
public abstract class BaseLanguage {
    public final String extension;
    protected final Core runner;
    
    public BaseLanguage(String extension, Core runner) {
        this.extension = extension;
        this.runner = runner;
    }
    
    public Thread trigger(ScriptTrigger macro, BaseEvent event, Runnable then,
                          Consumer<Throwable> catcher) {
        
        final ScriptTrigger staticMacro = macro.copy();
        final Thread t = new Thread(() -> {
            ScriptThreadWrapper th = new ScriptThreadWrapper(Thread.currentThread(), staticMacro, System.currentTimeMillis());
            try {
                runner.threads.putIfAbsent(staticMacro, new ArrayList<>());
                Thread.currentThread().setName(staticMacro.triggerType.toString() + " " + staticMacro.event + " " + staticMacro.scriptFile
                    + ": " + runner.threads.get(staticMacro).size());
                runner.threads.get(staticMacro).add(th);
                File file = new File(runner.config.macroFolder, staticMacro.scriptFile);
                if (file.exists()) {
                    
                    exec(staticMacro, file, event);
                    
                    if (then != null) then.run();
                }
            } catch (Exception e) {
                runner.profile.logError(e);
                if (catcher != null) catcher.accept(e);
                e.printStackTrace();
            } finally {
                runner.removeThread(th);
            }
        });
        
        t.start();
        return t;
    }
    
    public Map<String, BaseLibrary> retrieveLibs(Object context) {
        return runner.libraryRegistry.getLibraries(this, context, Thread.currentThread());
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
    public abstract void exec(ScriptTrigger macro, File file, BaseEvent event) throws Exception;
    
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
    public abstract void exec(String script, Map<String, Object> globals, Path currentDir) throws Exception;
    
    @Deprecated
    public String extension() {
        return extension;
    }
    
}