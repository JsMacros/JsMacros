package xyz.wagyourtail.jsmacros.core.language.impl;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavascriptLanguageDefinition extends BaseLanguage {
    private static final Builder build = Context.newBuilder("js")
        .allowHostAccess(HostAccess.ALL)
        .allowHostClassLookup(s -> true)
        .allowAllAccess(true)
        .allowIO(true)
        .allowExperimentalOptions(true)
        .option("js.commonjs-require", "true");
    
    public JavascriptLanguageDefinition(String extension, Core runner) {
        super(extension, runner);
    }
    
    private Context buildContext(Path currentDir, Map<String, Object> globals) {
        if (runner.config.options.extraJsOptions == null)
            runner.config.options.extraJsOptions = new LinkedHashMap<>();
        build.options(runner.config.options.extraJsOptions);
        if (currentDir != null) build.currentWorkingDirectory(currentDir);
        final Context con = build.build();
        
        // Set Bindings
        final Value binds = con.getBindings("js");
        
        if (globals != null) globals.forEach(binds::putMember);
        
        retrieveLibs(con).forEach(binds::putMember);
        
        return con;
    }
    
    @Override
    public void exec(ScriptTrigger macro, File file, BaseEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();
        
        globals.put("event", event);
        globals.put("file", file);
        
        final Context con = buildContext(file.getParentFile().toPath(), globals);
        con.eval(Source.newBuilder("js", file).build());
    }
    
    @Override
    public void exec(String script, Map<String, Object> globals, Path currentDir) throws Exception {
        final Context con = buildContext(currentDir, globals);
        con.eval("js", script);
    }
    
}
