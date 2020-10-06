package xyz.wagyourtail.jsmacros.runscript;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import xyz.wagyourtail.jsmacros.api.functions.FConsumer;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.extensionbase.Functions;
import xyz.wagyourtail.jsmacros.extensionbase.ILanguage;

public class JavascriptLanguageDefinition implements ILanguage {

    @Override
    public void exec(RawMacro macro, File file, IEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();          
        
        globals.put("event", event);
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
        
        for (Functions f : RunScript.standardLib) {
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
}
