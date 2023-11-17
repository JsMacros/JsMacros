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
import xyz.wagyourtail.jsmacros.js.GraalConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraalLanguageDefinition extends BaseLanguage<Context, GraalScriptContext> {
    public static final Engine engine = Engine.newBuilder().option("engine.WarnInterpreterOnly", "false").build();
    public static final boolean isJsInstalled = engine.getLanguages().containsKey("js");

    public GraalLanguageDefinition(Extension extension, Core<?, ?> runner) {
        super(extension, runner);
    }

    protected Context buildContext(File currentDir, String lang, Map<String, String> extraJsOptions, Map<String, Object> globals, Map<String, BaseLibrary> libs) throws IOException {

        Builder build = Context.newBuilder()
                .engine(engine)
                .allowAllAccess(true)
                .allowExperimentalOptions(true);

        for (Map.Entry<String, String> e : extraJsOptions.entrySet()) {
            try {
                build.option(e.getKey(), e.getValue());
            } catch (IllegalArgumentException ex) {
                Core.getInstance().profile.logError(new RuntimeException("Invalid GraalVM option: " + e.getKey() + " = " + e.getValue(), ex));
            }
        }

        if (currentDir == null) {
            currentDir = runner.config.macroFolder;
        }
        build.currentWorkingDirectory(currentDir.toPath().toAbsolutePath());

        if (isJsInstalled) {
            build.option("js.commonjs-require", "true");
            build.option("js.commonjs-require-cwd", currentDir.getCanonicalPath());
        }

        final Context con = build.build();

        // Set Bindings
        final Value binds = con.getBindings(lang);

        if (globals != null) {
            globals.forEach(binds::putMember);
        }

        libs.forEach(binds::putMember);

        return con;
    }

    @Override
    protected void exec(EventContainer<GraalScriptContext> ctx, ScriptTrigger macro, BaseEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();

        globals.put("event", event);
        globals.put("file", ctx.getCtx().getFile());
        globals.put("context", ctx);

        final GraalConfig conf = runner.config.getOptions(GraalConfig.class);
        if (conf.extraGraalOptions == null) {
            conf.extraGraalOptions = new LinkedHashMap<>();
        }

        Map<String, BaseLibrary> lib = retrieveLibs(ctx.getCtx());
        String lang = Source.findLanguage(ctx.getCtx().getFile());
        if (!engine.getLanguages().containsKey(lang)) {
            if (isJsInstalled) {
                lang = "js";
            } else {
                lang = engine.getLanguages().keySet().stream().findFirst().orElseThrow(() -> new RuntimeException("No GraalVM languages installed!"));
            }
        }
        final Context con = buildContext(ctx.getCtx().getContainedFolder(), lang, conf.extraGraalOptions, globals, lib);
        ctx.getCtx().setContext(con);
        con.enter();
        try {
            assert ctx.getCtx().getFile() != null;
            con.eval(Source.newBuilder(lang, ctx.getCtx().getFile()).build());
        } finally {
            try {
                con.leave();
                ctx.getCtx().tasks.poll();
                WrappedThread next = ctx.getCtx().tasks.peek();
                if (next != null) {
                    next.notifyReady();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void exec(EventContainer<GraalScriptContext> ctx, String lang, String script, BaseEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();

        globals.put("event", event);
        globals.put("file", ctx.getCtx().getFile());
        globals.put("context", ctx);

        final GraalConfig conf = runner.config.getOptions(GraalConfig.class);
        if (conf.extraGraalOptions == null) {
            conf.extraGraalOptions = new LinkedHashMap<>();
        }

        Map<String, BaseLibrary> lib = retrieveLibs(ctx.getCtx());
        lang = Source.findLanguage(new File(lang.startsWith(".") ? lang : "." + lang));
        if (!engine.getLanguages().containsKey(lang)) {
            if (isJsInstalled) {
                lang = "js";
            } else {
                lang = engine.getLanguages().keySet().stream().findFirst().orElseThrow(() -> new RuntimeException("No GraalVM languages installed!"));
            }
        }
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
            try {
                con.leave();
                ctx.getCtx().tasks.poll();
                WrappedThread next = ctx.getCtx().tasks.peek();
                if (next != null) {
                    next.notifyReady();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public GraalScriptContext createContext(BaseEvent event, File file) {
        return new GraalScriptContext(event, file);
    }

}
