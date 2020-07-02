package xyz.wagyourtail.jsmacros.runscript;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.python.util.PythonInterpreter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.runscript.functions.chatFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.globalVarFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.jsMacrosFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.keybindFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.playerFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.timeFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.worldFunctions;

public class RunScript {
    public static HashMap<RawMacro, ArrayList<Thread>> threads = new HashMap<>();
    public static String language = "js";
    
    
    public static Thread exec(RawMacro macro, String event, HashMap<String, Object> args) {
        if (macro.scriptFile.endsWith(".py")) {
            return execPY(macro, event, args);
        } else {
            return execJS(macro, event, args);
        }
    }
    
    public static Thread execPY(RawMacro macro, String event, HashMap<String, Object> args) {
        Thread t = new Thread(() -> {
            try {
                File file = new File(jsMacros.config.macroFolder, macro.scriptFile);
                if (file.exists()) {
                    PythonInterpreter interp = new PythonInterpreter();
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
                    interp.exec("import os\nos.chdir('"+file.getParentFile().getCanonicalPath().replaceAll("\\\\", "/")+"')");
                    interp.execfile(file.getCanonicalPath());
                    interp.close();
                }
            } catch (Exception e) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.inGameHud != null) {
                    LiteralText text = new LiteralText(e.toString());
                    mc.inGameHud.getChatHud().addMessage(text, 0);
                }
                e.printStackTrace();
            }
        });
        
        t.start();
        return t;
    }
    
    public static Thread execJS(RawMacro macro, String event, HashMap<String, Object> args) {
        Thread t = new Thread(() -> {
            threads.putIfAbsent(macro, new ArrayList<>());
            Thread.currentThread().setName(macro.type.toString() + " " + macro.eventkey + " " + macro.scriptFile + ": " + threads.get(macro).size());
            threads.get(macro).add(Thread.currentThread());
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

                    //Run Script
                    
                    con.eval(language, "load(\"./"+file.getName()+"\")");
//                    engine.eval(new FileReader(file));
                }
            } catch (Exception e) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.inGameHud != null) {
                    LiteralText text = new LiteralText(e.toString());
                    mc.inGameHud.getChatHud().addMessage(text, 0);
                }
                e.printStackTrace();
            }
            threads.get(macro).remove(Thread.currentThread());
        });
        
        
        t.start();
        return t;
    }
}