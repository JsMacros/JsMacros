package xyz.wagyourtail.jsmacros.runscript;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.python.util.PythonInterpreter;

import jep.SharedInterpreter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.runscript.functions.chatFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.fsFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.globalVarFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.jsMacrosFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.keybindFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.playerFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.requestFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.timeFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.worldFunctions;

public class RunScript {
    public static Map<RawMacro, List<thread>> threads = new HashMap<>();
    public static String language = "js";
    
    public static ArrayList<thread> getThreads() {
        ArrayList<thread> th = new ArrayList<>();
        for (List<thread> tl : threads.values()) {
            th.addAll(tl);
        }
        return th;
    }
    
    public static void removeThread(thread t) {
        if (threads.containsKey(t.m)) {
            threads.get(t.m).remove(t);
        }
    }
    
    public static Thread exec(RawMacro macro, String event, Map<String, Object> args) {
        return exec(macro, event, args, null, null);
    }
    
    public static Thread exec(RawMacro macro, String event, Map<String, Object> args, Runnable then, Consumer<String> catcher) {
        if (macro.scriptFile.endsWith(".py")) {
            if (jsMacros.config.options.enableJEP && !macro.scriptFile.toLowerCase().endsWith("jython.py")) return execJEP(macro, event, args, then, catcher);
            else return execJython(macro, event, args, then, catcher);
        } else {
            return execJS(macro, event, args, then, catcher);
        }
    }
    
    
    
    public static Thread execJEP(RawMacro macro, String event, Map<String, Object> args, Runnable then, Consumer<String> catcher) {
        Thread t = new Thread(() -> {
            threads.putIfAbsent(macro, new ArrayList<>());
            Thread.currentThread().setName(macro.type.toString() + " " + macro.eventkey + " " + macro.scriptFile + ": " + threads.get(macro).size());
            thread th = new thread(Thread.currentThread(), macro, System.currentTimeMillis());
            threads.get(macro).add(th);
            try (SharedInterpreter interp = new SharedInterpreter()){
                File file = new File(jsMacros.config.macroFolder, macro.scriptFile);
                if (file.exists()) {
                    interp.set("event", (Object)event);
                    interp.set("args", args);
                    interp.set("file", file);
                    interp.set("globalvars", new globalVarFunctions());
                    interp.set("jsmacros", new jsMacrosFunctions());
                    //interp.set("time", new timeFunctions());
                    interp.set("keybind", new keybindFunctions());
                    interp.set("chat", new chatFunctions());
                    interp.set("world", new worldFunctions());
                    interp.set("player", new playerFunctions());
                    interp.set("hud", new hudFunctions());
                    interp.set("request", new requestFunctions());
                    interp.exec("import os\nos.chdir('"+file.getParentFile().getCanonicalPath().replaceAll("\\\\", "/")+"')");
                    interp.runScript(file.getCanonicalPath());
                    if (then != null) then.run();
                }
            } catch(Exception e) {
                if (e.getCause() instanceof ThreadDeath) {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.inGameHud != null) {
                        mc.inGameHud.getChatHud().addMessage(new LiteralText("JEP exception: java.lang.ThreadDeath"), 0);
                    }  
                } else {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.inGameHud != null) {
                        LiteralText text = new LiteralText(e.toString());
                        mc.inGameHud.getChatHud().addMessage(text, 0);
                    }
                }
                if (catcher != null) catcher.accept(e.toString());
                e.printStackTrace();
            }
            removeThread(th);
        });
        
        t.start();
        return t;
    }
    
    public static Thread execJython(RawMacro macro, String event, Map<String, Object> args, Runnable then, Consumer<String> catcher) {
        Thread t = new Thread(() -> {
            threads.putIfAbsent(macro, new ArrayList<>());
            Thread.currentThread().setName(macro.type.toString() + " " + macro.eventkey + " " + macro.scriptFile + ": " + threads.get(macro).size());
            thread th = new thread(Thread.currentThread(), macro, System.currentTimeMillis());
            threads.get(macro).add(th);
            try (PythonInterpreter interp = new PythonInterpreter()) {
                File file = new File(jsMacros.config.macroFolder, macro.scriptFile);
                if (file.exists()) {
                    interp.set("event", event);
                    interp.set("args", args);
                    interp.set("file", file);
                    interp.set("globalvars", new globalVarFunctions());
                    interp.set("jsmacros", new jsMacrosFunctions());
                    //interp.set("time", new timeFunctions());
                    interp.set("keybind", new keybindFunctions());
                    interp.set("chat", new chatFunctions());
                    interp.set("world", new worldFunctions());
                    interp.set("player", new playerFunctions());
                    interp.set("hud", new hudFunctions());
                    interp.set("request", new requestFunctions());
                    interp.exec("import os\nos.chdir('"+file.getParentFile().getCanonicalPath().replaceAll("\\\\", "/")+"')");
                    interp.execfile(file.getCanonicalPath());
                    if (then != null) then.run();
                }
            } catch(Exception e) {
                if (e.getCause() instanceof ThreadDeath) {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.inGameHud != null) {
                        mc.inGameHud.getChatHud().addMessage(new LiteralText("Jython exception: java.lang.ThreadDeath"), 0);
                    }  
                } else {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.inGameHud != null) {
                        LiteralText text = new LiteralText(e.toString());
                        mc.inGameHud.getChatHud().addMessage(text, 0);
                    }
                }
                if (catcher != null) catcher.accept(e.toString());
                e.printStackTrace();
            }
            removeThread(th);
        });
        
        t.start();
        return t;
    }
    
    public static Thread execJS(RawMacro macro, String event, Map<String, Object> args, Runnable then, Consumer<String> catcher) {
        Thread t = new Thread(() -> {
            threads.putIfAbsent(macro, new ArrayList<>());
            Thread.currentThread().setName(macro.type.toString() + " " + macro.eventkey + " " + macro.scriptFile + ": " + threads.get(macro).size());
            thread th = new thread(Thread.currentThread(), macro, System.currentTimeMillis());
            threads.get(macro).add(th);
            try {
                File file = new File(jsMacros.config.macroFolder, macro.scriptFile);
                if (file.exists()) {
                    
                    //Build Context
                    
                    Builder build = Context.newBuilder(language);
                    build.allowHostAccess(HostAccess.ALL);
                    build.allowHostClassLookup(s -> true);
                    build.allowAllAccess(true);
                    build.allowExperimentalOptions(true);
                    build.currentWorkingDirectory(Paths.get(file.getParent()));
                    Context con = build.build();
                    
                    //Set Bindings
                    
                    Value binds = con.getBindings(language);
//                    ScriptEngine engine = GraalJSScriptEngine.create(null, build);
                    binds.putMember("event", event);
                    binds.putMember("args", args);
                    binds.putMember("file", file);
                    binds.putMember("globalvars", new globalVarFunctions());
                    binds.putMember("jsmacros", new jsMacrosFunctions());
                    binds.putMember("time", new timeFunctions());
                    binds.putMember("keybind", new keybindFunctions());
                    binds.putMember("chat", new chatFunctions());
                    binds.putMember("world", new worldFunctions());
                    binds.putMember("player", new playerFunctions());
                    binds.putMember("hud", new hudFunctions());
                    binds.putMember("request", new requestFunctions());
                    binds.putMember("fs", new fsFunctions());

                    //Run Script
                    
                    con.eval(language, "load(\"./"+file.getName()+"\")");
                    if (then != null) then.run();
//                    engine.eval(new FileReader(file));
                }
            } catch (Exception e) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.inGameHud != null) {
                    LiteralText text = new LiteralText(e.toString());
                    mc.inGameHud.getChatHud().addMessage(text, 0);
                }
                if (catcher != null) catcher.accept(e.toString());
                e.printStackTrace();
            }
            removeThread(th);
        });
        
        
        t.start();
        return t;
    }
    
    public static class thread {
        public Thread t;
        public RawMacro m;
        public long startTime;
        
        public thread(Thread t, RawMacro m, long startTime) {
            this.t = t;
            this.m = m;
            this.startTime = startTime;
        }
        
        public void start() {
            t.start();
        }
    }
}