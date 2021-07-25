package xyz.wagyourtail.jsmacros.core.language.impl;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.Context.Builder;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.impl.FWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavascriptLanguageDefinition extends BaseLanguage<Context> {
    private static final Engine engine = Engine.create();
    
    public JavascriptLanguageDefinition(String extension, Core runner) {
        super(extension, runner);
    }
    
    protected Context buildContext(Path currentDir, Map<String, String> extraJsOptions, Map<String, Object> globals, Map<String, BaseLibrary> libs) throws IOException {

        Builder build = Context.newBuilder("js")
            .engine(engine)
            .allowAllAccess(true)
            .allowExperimentalOptions(true)
            .option("js.commonjs-require", "true");


        build.options(extraJsOptions);
        if (currentDir == null) {
            currentDir = runner.config.macroFolder.toPath();
        }
        build.currentWorkingDirectory(currentDir);
        build.option("js.commonjs-require-cwd", currentDir.toFile().getCanonicalPath());
        
        final Context con = build.build();
        
        // Set Bindings
        final Value binds = con.getBindings("js");
        
        if (globals != null) globals.forEach(binds::putMember);

        libs.forEach(binds::putMember);

        return con;
    }
    
    @Override
    protected void exec(EventContainer<Context> ctx, ScriptTrigger macro, File file, BaseEvent event) throws Exception {
        Map<String, Object> globals = new HashMap<>();
        
        globals.put("event", event);
        globals.put("file", file);
        globals.put("context", ctx);

        final CoreConfigV2 conf = runner.config.getOptions(CoreConfigV2.class);
        if (conf.extraJsOptions == null)
            conf.extraJsOptions = new LinkedHashMap<>();

        Map<String, BaseLibrary> lib = retrieveLibs(ctx.getCtx());

        final Context con = buildContext(file.getParentFile().toPath(), conf.extraJsOptions, globals, lib);
        ctx.getCtx().setContext(con);
        con.enter();
        try {
            con.eval(Source.newBuilder("js", file).build());
        } finally {
            con.leave();
            ((FWrapper) lib.get("JavaWrapper")).tasks.poll().release();
        }
    }
    
    @Override
    protected void exec(EventContainer<Context> ctx, String script, Map<String, Object> globals, Path currentDir) throws Exception {
        globals.put("context", ctx);

        final CoreConfigV2 conf = runner.config.getOptions(CoreConfigV2.class);
        if (conf.extraJsOptions == null)
            conf.extraJsOptions = new LinkedHashMap<>();

        Map<String, BaseLibrary> lib = retrieveLibs(ctx.getCtx());

        final Context con = buildContext(currentDir, conf.extraJsOptions, globals, lib);
        ctx.getCtx().setContext(con);
        con.enter();
        try {
            con.eval("js", script);
        } finally {
            con.leave();
            ((FWrapper) lib.get("JavaWrapper")).tasks.poll().release();
        }
    }
    
    @Override
    public BaseWrappedException<?> wrapException(Throwable ex) {
        if (ex instanceof PolyglotException) {
            Iterator<PolyglotException.StackFrame> frames = ((PolyglotException) ex).getPolyglotStackTrace().iterator();
            String message;
            if (((PolyglotException) ex).isHostException()) {
                message = ((PolyglotException) ex).asHostException().getClass().getName();
                String intMessage = ((PolyglotException) ex).asHostException().getMessage();
                if (intMessage != null) {
                    message += ": " + intMessage;
                }
            } else {
                message = ex.getMessage();
                if (message == null) {
                    message = "UnknownGuestException";
                }
            }
            return new BaseWrappedException<>(ex, message, wrapLocation(((PolyglotException) ex).getSourceLocation()), frames.hasNext() ? internalWrap(frames.next(), frames) : null);
        }
        return null;
    }
    
    @Override
    public JSScriptContext createContext(BaseEvent event, File file) {
        return new JSScriptContext(event, file);
    }
    
    private BaseWrappedException<?> internalWrap(PolyglotException.StackFrame current, Iterator<PolyglotException.StackFrame> frames) {
        if (current == null) return null;
        if (current.isGuestFrame()) {
            return new BaseWrappedException<>(current, " at " + current.getRootName(), wrapLocation(current.getSourceLocation()), frames.hasNext() ? internalWrap(frames.next(), frames) : null);
        }
        if (current.toHostFrame().getClassName().equals("org.graalvm.polyglot.Context") && current.toHostFrame().getMethodName().equals("eval")) return null;
        return BaseWrappedException.wrapHostElement(current.toHostFrame(), frames.hasNext() ? internalWrap(frames.next(), frames) : null);
    }
    
    private BaseWrappedException.SourceLocation wrapLocation(SourceSection pos) {
        BaseWrappedException.SourceLocation loc = null;
        if (pos != null) {
            if (pos.getSource().getPath() != null) {
                loc = new BaseWrappedException.GuestLocation(new File(pos.getSource().getPath()), pos.getCharIndex(), pos.getCharEndIndex(), pos.getStartLine(), pos.getStartColumn());
            } else {
                loc = new BaseWrappedException.HostLocation(String.format("%s %d:%d", pos.getSource().getName(), pos.getStartLine(), pos.getStartColumn()));
            }
        }
        return loc;
    }
}
