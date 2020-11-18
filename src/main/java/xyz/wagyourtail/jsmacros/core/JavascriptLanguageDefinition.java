package xyz.wagyourtail.jsmacros.core;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import xyz.wagyourtail.jsmacros.core.config.ConfigManager;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavascriptLanguageDefinition implements ILanguage {
    private static final Builder build = Context.newBuilder("js")
        .allowHostAccess(HostAccess.ALL)
        .allowHostClassLookup(s -> true)
        .allowAllAccess(true)
        .allowIO(true)
        .allowExperimentalOptions(true);

    @Override
    public void exec(ScriptTrigger macro, File file, BaseEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();          
        
        globals.put("event", event);
        globals.put("file", file);

        final Context con = buildContext(file.getParentFile().toPath(), globals);
        con.eval(Source.newBuilder("js", file).build());
    }

    private Context buildContext(Path currentDir, Map<String, Object> globals) {
        if (ConfigManager.INSTANCE.options.extraJsOptions == null) ConfigManager.INSTANCE.options.extraJsOptions = new LinkedHashMap<>();
        build.options(ConfigManager.INSTANCE.options.extraJsOptions);
        if (currentDir != null) build.currentWorkingDirectory(currentDir);
        final Context con = build.build();

        // Set Bindings
        final Value binds = con.getBindings("js");

        if (globals != null) globals.forEach(binds::putMember);
        
        RunScript.libraryRegistry.getLibraries(extension()).forEach(binds::putMember);

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
