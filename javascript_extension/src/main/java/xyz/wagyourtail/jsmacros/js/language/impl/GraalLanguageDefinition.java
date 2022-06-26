package xyz.wagyourtail.jsmacros.js.language.impl;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.extensions.Extension;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.js.JSConfig;
import xyz.wagyourtail.jsmacros.js.library.impl.FWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class GraalLanguageDefinition extends BaseLanguage<Context> {
    public static final Engine engine = Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build();
    
    public GraalLanguageDefinition(Extension extension, Core<?, ?> runner) {
        super(extension, runner);
    }
    
    protected Context buildContext(File currentDir, String lang, Map<String, String> extraJsOptions, Map<String, Object> globals, Map<String, BaseLibrary> libs) throws IOException {

        Builder build = Context.newBuilder()
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
        final Value binds = con.getBindings(lang);
        
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

        final JSConfig conf = runner.config.getOptions(JSConfig.class);
        if (conf.extraGraalOptions == null)
            conf.extraGraalOptions = new LinkedHashMap<>();

        Map<String, BaseLibrary> lib = retrieveLibs(ctx.getCtx());
        String lang = Source.findLanguage(ctx.getCtx().getFile());
        if (!engine.getLanguages().containsKey(lang)) lang = "js";
        final Context con = buildContext(ctx.getCtx().getContainedFolder(), lang, conf.extraGraalOptions, globals, lib);
        ctx.getCtx().setContext(con);
        con.enter();
        try {
            assert ctx.getCtx().getFile() != null;
            con.eval(Source.newBuilder(lang, ctx.getCtx().getFile()).build());
        } finally {
            con.leave();
            Objects.requireNonNull(((FWrapper) lib.get("JavaWrapper")).tasks.poll()).release();
        }
    }
    
    @Override
    protected void exec(EventContainer<Context> ctx, String lang, String script, BaseEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();

        globals.put("event", event);
        globals.put("file", ctx.getCtx().getFile());
        globals.put("context", ctx);

        final JSConfig conf = runner.config.getOptions(JSConfig.class);
        if (conf.extraGraalOptions == null)
            conf.extraGraalOptions = new LinkedHashMap<>();

        Map<String, BaseLibrary> lib = retrieveLibs(ctx.getCtx());
        lang = Source.findLanguage(new File(lang));
        if (!engine.getLanguages().containsKey(lang)) lang = "js";
        final Context con = buildContext(ctx.getCtx().getContainedFolder(), lang, conf.extraGraalOptions, globals, lib);
        ctx.getCtx().setContext(con);
        con.enter();
        try {
            if (ctx.getCtx().getFile() != null) {
                con.eval(Source.newBuilder(lang, ctx.getCtx().getFile()).content(script).build());
            } else {
                con.eval(lang, script);
            }
        } finally {
            con.leave();
            Objects.requireNonNull(((FWrapper) lib.get("JavaWrapper")).tasks.poll()).release();
        }
    }

    @Override
    public GraalScriptContext createContext(BaseEvent event, File file) {
        return new GraalScriptContext(event, file);
    }
}
