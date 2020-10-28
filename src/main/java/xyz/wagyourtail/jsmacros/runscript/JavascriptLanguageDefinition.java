package xyz.wagyourtail.jsmacros.runscript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.api.functions.FConsumer;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.extensionbase.Functions;
import xyz.wagyourtail.jsmacros.extensionbase.ILanguage;

public class JavascriptLanguageDefinition implements ILanguage {
    private static final Builder build = Context.newBuilder("js")
        .allowHostAccess(HostAccess.ALL)
        .allowHostClassLookup(s -> true)
        .allowAllAccess(true)
        .allowIO(true)
        .allowExperimentalOptions(true);

    @Override
    public void exec(RawMacro macro, File file, IEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();          
        
        globals.put("event", event);
        globals.put("file", file);

        final Context con = buildContext(file.getParentFile().toPath(), globals);
        con.eval(Source.newBuilder("js", file).build());
    }

    private Context buildContext(Path currentDir, Map<String, Object> globals) {
        if (JsMacros.config.options.extraJsOptions == null) JsMacros.config.options.extraJsOptions = new LinkedHashMap<>();
        build.options(JsMacros.config.options.extraJsOptions);
        if (currentDir != null) build.currentWorkingDirectory(currentDir);
        final Context con = build.build();

        // Set Bindings
        final Value binds = con.getBindings("js");

        if (globals != null) for (Map.Entry<String, Object> e : globals.entrySet()) {
            binds.putMember(e.getKey(), e.getValue());
        }

        for (Functions f : RunScript.standardLib) {
            if (!f.excludeLanguages.contains(".js")) {
                binds.putMember(f.libName, f);
            }
        }
        binds.putMember("consumer", new FConsumer("consumer"));

        return con;
    }

    @Override
    public void exec(String script, Map<String, Object> globals, Path currentDir) throws Exception {
        final Context con = buildContext(currentDir, globals);
        con.eval("js", script);
    }
    
    @Override
    public String extension() {
        return ".js";
    }
}
