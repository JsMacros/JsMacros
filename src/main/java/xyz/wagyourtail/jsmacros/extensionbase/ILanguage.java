package xyz.wagyourtail.jsmacros.extensionbase;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.access.IChatHud;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.runscript.RunScript;
import xyz.wagyourtail.jsmacros.runscript.RunScript.ScriptThreadWrapper;

public interface ILanguage {
    
    public default Thread trigger(RawMacro macro, IEvent event, Runnable then,
        Consumer<String> catcher) {
        
        final RawMacro staticMacro = (RawMacro) macro.copy();
        final Thread t = new Thread(() -> {
            ScriptThreadWrapper th = new ScriptThreadWrapper(Thread.currentThread(), staticMacro, System.currentTimeMillis());
            try {
                RunScript.threads.putIfAbsent(staticMacro, new ArrayList<>());
                Thread.currentThread().setName(staticMacro.type.toString() + " " + staticMacro.eventkey + " " + staticMacro.scriptFile
                    + ": " + RunScript.threads.get(staticMacro).size());
                RunScript.threads.get(staticMacro).add(th);
                File file = new File(JsMacros.config.macroFolder, staticMacro.scriptFile);
                if (file.exists()) {
                    
                    exec(staticMacro, file, event);
                    
                    if (then != null) then.run();
                }
            } catch (Exception e) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.inGameHud != null) {
                    LiteralText text = new LiteralText(e.toString());
                    ((IChatHud)mc.inGameHud.getChatHud()).jsmacros_addMessageBypass(text);
                }
                if (catcher != null) catcher.accept(e.toString());
                e.printStackTrace();
            } finally {
                RunScript.removeThread(th);
            }
        });
        
        t.start();
        return t;
    }
    
    public void exec(RawMacro macro, File file, IEvent event) throws Exception;
    
    public void exec(String script, Map<String, Object> globals, Path currentDir) throws Exception;
    
    public String extension();
}