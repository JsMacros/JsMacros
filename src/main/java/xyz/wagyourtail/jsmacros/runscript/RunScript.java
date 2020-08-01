package xyz.wagyourtail.jsmacros.runscript;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public static Language defaultLang;
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
        Language js = new Language() {
            @Override
            public void exec(RawMacro macro, File file, String event, Map<String, Object> args) throws Exception {
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
                            
            }

            @Override
            public String extension() {
                return ".js";
            }
        };
        addLanguage(js);
        defaultLang = js;

        // ------------------- JYTHON ------------------------ //
        Language jython = new Language() {
            @Override
            public void exec(RawMacro macro, File file, String event, Map<String, Object> args) throws Exception {
                try (PythonInterpreter interp = new PythonInterpreter()) {
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
                } catch (Exception e) {
                    throw e;
                }
            }

            @Override
            public String extension() {
                return jsMacros.config.options.enableJEP ? "jython.py" : ".py";
            }
        };
        addLanguage(jython);


        // -------------------- JEP -------------------------- //
        if (jsMacros.config.options.enableJEP) {
            Language jep = new Language() {
                @Override
                public void exec(RawMacro macro, File file, String event, Map<String, Object> args) throws Exception {
                    try (SharedInterpreter interp = new SharedInterpreter()) {
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
                    } catch(Exception e) {
                        throw e;
                    }
                }

                @Override
                public String extension() {
                    return ".py";
                }
            };
            addLanguage(jep);
        }
        sortLanguages();
    }

    public static void addLanguage(Language l) {
        languages.add(l);
    }
    
    public static void sortLanguages() {
        Collections.sort(languages, new sortLanguage());
        
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
                return language.trigger(macro, event, args, then, catcher);
        }
        return defaultLang.trigger(macro, event, args, then, catcher);
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
        
        public default Thread trigger(RawMacro macro, String event, Map<String, Object> args, Runnable then,
            Consumer<String> catcher) {
            Thread t = new Thread(() -> {
                RunScript.threads.putIfAbsent(macro, new ArrayList<>());
                Thread.currentThread().setName(macro.type.toString() + " " + macro.eventkey + " " + macro.scriptFile
                    + ": " + RunScript.threads.get(macro).size());
                thread th = new thread(Thread.currentThread(), macro, System.currentTimeMillis());
                RunScript.threads.get(macro).add(th);
                try {
                    File file = new File(jsMacros.config.macroFolder, macro.scriptFile);
                    if (file.exists()) {
                        
                        exec(macro, file, event, args);
                        
                        if (then != null) then.run();
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
                RunScript.removeThread(th);
            });


            t.start();
            return t;
        }
        
        public void exec(RawMacro macro, File file, String event, Map<String, Object> args) throws Exception;

        public String extension();
    }
    
    public static class sortLanguage implements Comparator<Language> {

        @Override
        public int compare(Language a, Language b) {
            if (b.extension().endsWith(a.extension())) return 1;
            return -1;
        }
        
    }
}
