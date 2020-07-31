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
import xyz.wagyourtail.jsmacros.runscript.functions.*;

public class RunScript {
    public static Map<RawMacro, List<thread>> threads = new HashMap<>();
    public static List<Language> languages = new ArrayList<>();
    public static Language js;
    public static List<Functions> standardLib = new ArrayList<>();

    static {
        List<String> excludePython = new ArrayList<>();
        excludePython.add(".py");
        excludePython.add("jython.py");
        standardLib.add(new globalVarFunctions("globalvars"));
        standardLib.add(new jsMacrosFunctions("jsmacros"));
        standardLib.add(new timeFunctions("time", excludePython));
        standardLib.add(new keybindFunctions("keybind"));
        standardLib.add(new chatFunctions("chat"));
        standardLib.add(new worldFunctions("world"));
        standardLib.add(new playerFunctions("player"));
        standardLib.add(new hudFunctions("hud"));
        standardLib.add(new requestFunctions("request"));
        standardLib.add(new fsFunctions("fs", excludePython));


        // -------------------- JAVASCRIPT -------------------------- //
        js = new Language() {
            @Override
            public Thread exec(RawMacro macro, String event, Map<String, Object> args, Runnable then,
                Consumer<String> catcher) {
                Thread t = new Thread(() -> {
                    threads.putIfAbsent(macro, new ArrayList<>());
                    Thread.currentThread().setName(macro.type.toString() + " " + macro.eventkey + " " + macro.scriptFile
                        + ": " + threads.get(macro).size());
                    thread th = new thread(Thread.currentThread(), macro, System.currentTimeMillis());
                    threads.get(macro).add(th);
                    try {
                        File file = new File(jsMacros.config.macroFolder, macro.scriptFile);
                        if (file.exists()) {

                            // Build Context

                            Builder build = Context.newBuilder("js");
                            build.allowHostAccess(HostAccess.ALL);
                            build.allowHostClassLookup(s -> true);
                            build.allowAllAccess(true);
                            build.allowExperimentalOptions(true);
                            build.currentWorkingDirectory(Paths.get(file.getParent()));
                            Context con = build.build();

                            // Set Bindings

                            Value binds = con.getBindings("js");
                            // ScriptEngine engine = GraalJSScriptEngine.create(null, build);
                            binds.putMember("event", event);
                            binds.putMember("args", args);
                            binds.putMember("file", file);

                            for (Functions f : standardLib) {
                                if (!f.excludeLanguages.contains(".js")) {
                                    binds.putMember(f.libName, f);
                                }
                            }

                            // Run Script

                            con.eval("js", "load(\"./" + file.getName() + "\")");
                            if (then != null) then.run();
                            // engine.eval(new FileReader(file));
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

            @Override
            public String extension() {
                return ".js";
            }
        };
        languages.add(js);


        // ------------------- JYTHON ------------------------ //
        Language jython = new Language() {
            @Override
            public Thread exec(RawMacro macro, String event, Map<String, Object> args, Runnable then,
                Consumer<String> catcher) {
                Thread t = new Thread(() -> {
                    threads.putIfAbsent(macro, new ArrayList<>());
                    Thread.currentThread()
                        .setName(macro.type.toString() + " " + macro.eventkey + " " + macro.scriptFile + ": "
                            + threads.get(macro).size());
                    thread th = new thread(Thread.currentThread(), macro, System.currentTimeMillis());
                    threads.get(macro).add(th);
                    try (PythonInterpreter interp = new PythonInterpreter()) {
                        File file = new File(jsMacros.config.macroFolder, macro.scriptFile);
                        if (file.exists()) {
                            interp.set("event", event);
                            interp.set("args", args);
                            interp.set("file", file);

                            for (Functions f : standardLib) {
                                if (!f.excludeLanguages.contains("jython.py")) {
                                    interp.set(f.libName, f);
                                }
                            }

                            interp.exec("import os\nos.chdir('"
                                + file.getParentFile().getCanonicalPath().replaceAll("\\\\", "/") + "')");
                            interp.execfile(file.getCanonicalPath());
                            if (then != null) then.run();
                        }
                    } catch (Exception e) {
                        if (e.getCause() instanceof ThreadDeath) {
                            MinecraftClient mc = MinecraftClient.getInstance();
                            if (mc.inGameHud != null) {
                                mc.inGameHud.getChatHud().addMessage(
                                    new LiteralText("Jython exception: java.lang.ThreadDeath"),
                                    0);
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

            @Override
            public String extension() {
                return jsMacros.config.options.enableJEP ? "jython.py" : ".py";
            }
        };
        languages.add(jython);


        // -------------------- JEP -------------------------- //
        if (jsMacros.config.options.enableJEP) {
            Language jep = new Language() {
                @Override
                public Thread exec(RawMacro macro, String event, Map<String, Object> args, Runnable then,
                    Consumer<String> catcher) {
                    Thread t = new Thread(() -> {
                        threads.putIfAbsent(macro, new ArrayList<>());
                        Thread.currentThread().setName(macro.type.toString() + " " + macro.eventkey + " "
                            + macro.scriptFile + ": " + threads.get(macro).size());
                        thread th = new thread(Thread.currentThread(), macro, System.currentTimeMillis());
                        threads.get(macro).add(th);
                        try (SharedInterpreter interp = new SharedInterpreter()) {
                            File file = new File(jsMacros.config.macroFolder, macro.scriptFile);
                            if (file.exists()) {
                                interp.set("event", (Object) event);
                                interp.set("args", args);
                                interp.set("file", file);

                                for (Functions f : standardLib) {
                                    if (!f.excludeLanguages.contains(".py")) {
                                        interp.set(f.libName, f);
                                    }
                                }

                                interp.exec("import os\nos.chdir('"
                                    + file.getParentFile().getCanonicalPath().replaceAll("\\\\", "/") + "')");
                                interp.runScript(file.getCanonicalPath());
                                if (then != null) then.run();
                            }
                        } catch (Exception e) {
                            if (e.getCause() instanceof ThreadDeath) {
                                MinecraftClient mc = MinecraftClient.getInstance();
                                if (mc.inGameHud != null) {
                                    mc.inGameHud.getChatHud()
                                        .addMessage(new LiteralText("JEP exception: java.lang.ThreadDeath"), 0);
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

                @Override
                public String extension() {
                    return ".py";
                }
            };
            languages.add(jep);
        }
    }

    public static List<thread> getThreads() {
        List<thread> th = new ArrayList<>();
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

    public static Thread exec(RawMacro macro, String event, Map<String, Object> args, Runnable then,
        Consumer<String> catcher) {
        for (Language language : languages) {
            if (macro.scriptFile.endsWith(language.extension()))
                return language.exec(macro, event, args, then, catcher);
        }
        return js.exec(macro, event, args, then, catcher);
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

    public static interface Language {
        public Thread exec(RawMacro macro, String event, Map<String, Object> args, Runnable then,
            Consumer<String> catcher);

        public String extension();
    }
}
