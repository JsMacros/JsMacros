package xyz.wagyourtail.jsmacros.runscript;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.access.IChatHud;
import xyz.wagyourtail.jsmacros.api.Functions;
import xyz.wagyourtail.jsmacros.api.functions.*;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IRawMacro;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IScriptThreadWrapper;
import xyz.wagyourtail.jsmacros.config.RawMacro;

public class RunScript {
    public static Map<IRawMacro, List<IScriptThreadWrapper>> threads = new HashMap<>();
    public static List<Language> languages = new ArrayList<>();
    public static Language defaultLang;
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


        // -------------------- JAVASCRIPT -------------------------- //
        Language js = new Language() {
            
            @Override
            public void exec(RawMacro macro, File file, String event, Map<String, Object> args) throws Exception {
                Map<String, Object> globals = new HashMap<>();          
                
                globals.put("event", event);
                globals.put("args", args);
                globals.put("file", file);

                exec("load(\"./" + file.getName() + "\")", globals, file.getParentFile().toPath());
                            
            }

            @Override
            public void exec(String script, Map<String, Object> globals, Path currentDir) throws Exception {
             // Build Context

                Builder build = Context.newBuilder("js");
                build.allowHostAccess(HostAccess.ALL);
                build.allowHostClassLookup(s -> true);
                build.allowAllAccess(true);
                build.allowExperimentalOptions(true);
                if (currentDir != null) build.currentWorkingDirectory(currentDir);
                Context con = build.build();

                // Set Bindings
                Value binds = con.getBindings("js");

                if (globals != null) for (Map.Entry<String, Object> e : globals.entrySet()) {
                    binds.putMember(e.getKey(), e.getValue());
                }
                
                for (Functions f : standardLib) {
                    if (!f.excludeLanguages.contains(".js")) {
                        binds.putMember(f.libName, f);
                    }
                }
                binds.putMember("consumer", new FConsumer("consumer"));

                // Run Script

                con.eval("js", script);
            }
            
            @Override
            public String extension() {
                return ".js";
            }
        };
        addLanguage(js);
        defaultLang = js;
    }

    public static void addLanguage(Language l) {
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

    public static Thread exec(RawMacro macro, String event, Map<String, Object> args) {
        return exec(macro, event, args, null, null);
    }

    public static Thread exec(RawMacro macro, String event, Map<String, Object> args, Runnable then,
        Consumer<String> catcher) {
        for (Language language : languages) {
            if (macro.scriptFile.endsWith(language.extension()))
                return language.trigger(macro, event, args, then, catcher);
        }
        return defaultLang.trigger(macro, event, args, then, catcher);
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

    public static interface Language {
        
        public default Thread trigger(RawMacro macro, String event, Map<String, Object> args, Runnable then,
            Consumer<String> catcher) {
            
            final RawMacro staticMacro = (RawMacro) macro.copy();
            final Thread t = new Thread(() -> {
                ScriptThreadWrapper th = new ScriptThreadWrapper(Thread.currentThread(), staticMacro, System.currentTimeMillis());
                try {
                    RunScript.threads.putIfAbsent(staticMacro, new ArrayList<>());
                    Thread.currentThread().setName(staticMacro.type.toString() + " " + staticMacro.eventkey + " " + staticMacro.scriptFile
                        + ": " + RunScript.threads.get(staticMacro).size());
                    RunScript.threads.get(staticMacro).add(th);
                    File file = new File(jsMacros.config.macroFolder, staticMacro.scriptFile);
                    if (file.exists()) {
                        
                        exec(staticMacro, file, event, args);
                        
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
        
        public void exec(RawMacro macro, File file, String event, Map<String, Object> args) throws Exception;
        
        public void exec(String script, Map<String, Object> globals, Path currentDir) throws Exception;
        
        public String extension();
    }
    
    public static class sortLanguage implements Comparator<Language> {

        @Override
        public int compare(Language a, Language b) {
            String[] as = a.extension().replaceAll("\\.", " ").trim().split(" ");
            String[] bs = b.extension().replaceAll("\\.", " ").trim().split(" ");
            int lendif = bs.length-as.length;
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
