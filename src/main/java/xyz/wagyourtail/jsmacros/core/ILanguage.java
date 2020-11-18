package xyz.wagyourtail.jsmacros.core;

import xyz.wagyourtail.jsmacros.core.event.IEventTrigger;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.RunScript.ScriptThreadWrapper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Language interface for languages to be implemented on top of.
 * @since 1.1.3
 */
public interface ILanguage {
    
    public default Thread trigger(ScriptTrigger macro, BaseEvent event, Runnable then,
                                  Consumer<Throwable> catcher) {
        
        final ScriptTrigger staticMacro = (ScriptTrigger) macro.copy();
        final Thread t = new Thread(() -> {
            ScriptThreadWrapper th = new ScriptThreadWrapper(Thread.currentThread(), staticMacro, System.currentTimeMillis());
            try {
                RunScript.threads.putIfAbsent((IEventTrigger) staticMacro, new ArrayList<>());
                Thread.currentThread().setName(staticMacro.triggerType.toString() + " " + staticMacro.event + " " + staticMacro.scriptFile
                    + ": " + RunScript.threads.get(staticMacro).size());
                RunScript.threads.get(staticMacro).add(th);
                File file = new File(ConfigManager.INSTANCE.macroFolder, staticMacro.scriptFile);
                if (file.exists()) {
                    
                    exec(staticMacro, file, event);
                    
                    if (then != null) then.run();
                }
            } catch (Exception e) {
                ConfigManager.PROFILE.logError(e);
                if (catcher != null) catcher.accept(e);
                e.printStackTrace();
            } finally {
                RunScript.removeThread(th);
            }
        });
        
        t.start();
        return t;
    }
    
    /**
    *  run a script trigger/file with this.
    * @since 1.2.7 [citation needed]
     * @param macro
     * @param file
     * @param event
     *
     * @throws Exception
     */
    public void exec(ScriptTrigger macro, File file, BaseEvent event) throws Exception;
    
    /**
    *  run a string based script with this.
    *
    * @since 1.2.7 [citation needed]
     * @param script
     * @param globals
     * @param currentDir
     *
     * @throws Exception
     */
    public void exec(String script, Map<String, Object> globals, Path currentDir) throws Exception;
    
    public String extension();
}