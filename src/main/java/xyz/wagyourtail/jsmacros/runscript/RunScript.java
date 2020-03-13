package xyz.wagyourtail.jsmacros.runscript;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.runscript.functions.chatFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.jsMacrosFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.keybindFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.timeFunctions;

public class RunScript {
    public static HashMap<RawMacro, ArrayList<Thread>> threads = new HashMap<>();
    private static Builder context = Context.newBuilder("js");
    private static HashMap<String, Object> globals = new HashMap<>();
    private static ScriptEngine engine = GraalJSScriptEngine.create(null, context);
    private static HashMap<String, Object> globalBinds = new HashMap<>();
    static {
        context.allowHostAccess(HostAccess.ALL);
        context.allowHostClassLookup(s -> true);
        context.allowAllAccess(true);
        context.allowExperimentalOptions(true);
        globalBinds.put("global", globals);
        globalBinds.put("jsmacros", new jsMacrosFunctions());
        globalBinds.put("time", new timeFunctions());
        globalBinds.put("keybind", new keybindFunctions());
        globalBinds.put("chat", new chatFunctions());
    }

    public static Thread exec(RawMacro macro, String event, HashMap<String, Object> args) {
        File file = new File(jsMacros.config.macroFolder, macro.scriptFile);

        final Runnable r = new Runnable() {
            public void run() {
                try {
                    Bindings scriptBinds = new SimpleBindings(globalBinds);
                    scriptBinds.put("event", event);
                    scriptBinds.put("args", args);
                    scriptBinds.put("file", file);
                    engine.eval(new FileReader(file), scriptBinds);
                } catch (ScriptException | IOException e) {
                    if (jsMacros.getMinecraft().inGameHud != null) {
                        LiteralText text = new LiteralText(e.toString());
                        jsMacros.getMinecraft().inGameHud.getChatHud().addMessage(text);
                    } else {
                        e.printStackTrace();
                    }
                }
                threads.get(macro).remove(Thread.currentThread());
            }
        };

        Thread t = new Thread(r);

        // function files
        threads.putIfAbsent(macro, new ArrayList<>());
        t.setName(macro.type.toString() + " " + macro.eventkey + " " + macro.scriptFile + ": " + threads.get(macro).size());
        threads.get(macro).add(t);
        t.start();
        return t;
    }
}