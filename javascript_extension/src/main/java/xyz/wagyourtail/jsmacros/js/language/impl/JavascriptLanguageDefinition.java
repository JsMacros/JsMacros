package xyz.wagyourtail.jsmacros.js.language.impl;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.js.library.impl.FWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavascriptLanguageDefinition extends BaseLanguage<Context> {
    private static final Engine engine = Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build();
    
    public JavascriptLanguageDefinition(String extension, Core<?, ?> runner) {
        super(extension, runner);
    }
    
    protected Context buildContext(File currentDir, Map<String, String> extraJsOptions, Map<String, Object> globals, Map<String, BaseLibrary> libs) throws IOException {

        Builder build = Context.newBuilder("js")
            .engine(engine)
            .allowAllAccess(true)
            .allowExperimentalOptions(true)
            .option("js.commonjs-require", "true");


        build.options(extraJsOptions);
        if (currentDir == null) {
            currentDir = runner.config.macroFolder;
        }
        build.currentWorkingDirectory(currentDir.toPath().toAbsolutePath());
        build.option("js.commonjs-require-cwd", currentDir.getCanonicalPath());
        
        final Context con = build.build();
        
        // Set Bindings
        final Value binds = con.getBindings("js");
        
        if (globals != null) globals.forEach(binds::putMember);

        libs.forEach(binds::putMember);

        return con;
    }
    
    @Override
    protected void exec(EventContainer<Context> ctx, ScriptTrigger macro, BaseEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();
        
        globals.put("event", event);
        globals.put("file", ctx.getCtx().getFile());
        globals.put("context", ctx);

        final CoreConfigV2 conf = runner.config.getOptions(CoreConfigV2.class);
        if (conf.extraJsOptions == null)
            conf.extraJsOptions = new LinkedHashMap<>();

        Map<String, BaseLibrary> lib = retrieveLibs(ctx.getCtx());

        final Context con = buildContext(ctx.getCtx().getContainedFolder(), conf.extraJsOptions, globals, lib);
        ctx.getCtx().setContext(con);
        con.enter();
        try {
            con.eval(Source.newBuilder("js", ctx.getCtx().getFile()).build());
        } finally {
            con.leave();
            ((FWrapper) lib.get("JavaWrapper")).tasks.poll().release();
        }
    }
    
    @Override
    protected void exec(EventContainer<Context> ctx, String script, BaseEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();

        globals.put("event", event);
        globals.put("file", ctx.getCtx().getFile());
        globals.put("context", ctx);

        final CoreConfigV2 conf = runner.config.getOptions(CoreConfigV2.class);
        if (conf.extraJsOptions == null)
            conf.extraJsOptions = new LinkedHashMap<>();

        Map<String, BaseLibrary> lib = retrieveLibs(ctx.getCtx());

        final Context con = buildContext(ctx.getCtx().getContainedFolder(), conf.extraJsOptions, globals, lib);
        ctx.getCtx().setContext(con);
        con.enter();
        try {
            if (ctx.getCtx().getFile() != null) {
                con.eval(Source.newBuilder("js", ctx.getCtx().getFile()).content(script).build());
            } else {
                con.eval("js", script);
            }
        } finally {
            con.leave();
            ((FWrapper) lib.get("JavaWrapper")).tasks.poll().release();
        }
    }

    @Override
    public JSScriptContext createContext(BaseEvent event, File file) {
        return new JSScriptContext(event, file);
    }
}
