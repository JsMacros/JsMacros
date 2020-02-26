package xyz.wagyourtail.jsmacros.runscript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

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
    static {
        context.allowHostAccess(HostAccess.ALL);
        context.allowHostClassLookup(s -> true);
        context.allowAllAccess(true);
        context.allowExperimentalOptions(true);
    }
    
    public static Thread exec(RawMacro macro, HashMap<String, Object> args) {
        File file = macro.scriptFile;
        ScriptEngine engine = GraalJSScriptEngine.create(null, context);
        
        final Runnable r = new Runnable() {
            public void run() {
                try {
                    engine.eval(new FileReader(file));
                } catch (FileNotFoundException | ScriptException e) {
                    LiteralText text = new LiteralText(e.toString());
                    jsMacros.getMinecraft().inGameHud.getChatHud().addMessage(text);
                }
                threads.get(macro).remove(Thread.currentThread());
            }
        };
        
        Thread t = new Thread(r);
        
        engine.put("args", args);
        engine.put("file", file);
        engine.put("global", globals);
        
        //function files
        engine.put("jsmacros", new jsMacrosFunctions());
        engine.put("time", new timeFunctions());
        engine.put("binding", new keybindFunctions());
        engine.put("chat", new chatFunctions());
        threads.putIfAbsent(macro, new ArrayList<>());
        t.setName(macro.type.toString()+" "+macro.eventkey+" "+macro.scriptFileName()+": "+threads.get(macro).size());
        threads.get(macro).add(t);
        t.start();
        return t;
    }
}