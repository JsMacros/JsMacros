package xyz.wagyourtail.jsmacros.core.language.impl;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.Context.Builder;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavascriptLanguageDefinition extends BaseLanguage {
    private static final Engine engine = Engine.create();
    private static final Builder build = Context.newBuilder("js")
        .engine(engine)
        .allowHostAccess(HostAccess.ALL)
        .allowHostClassLookup(s -> true)
        .allowAllAccess(true)
        .allowIO(true)
        .allowExperimentalOptions(true)
        .option("js.commonjs-require", "true");
    
    public JavascriptLanguageDefinition(String extension, Core runner) {
        super(extension, runner);
    }
    
    private Context buildContext(Path currentDir, Map<String, Object> globals) throws IOException {
        if (runner.config.getOptions(CoreConfigV2.class).extraJsOptions == null)
            runner.config.getOptions(CoreConfigV2.class).extraJsOptions = new LinkedHashMap<>();
        build.options(runner.config.getOptions(CoreConfigV2.class).extraJsOptions);
        if (currentDir == null) {
            currentDir = runner.config.macroFolder.toPath();
        }
        build.currentWorkingDirectory(currentDir);
        build.option("js.commonjs-require-cwd", currentDir.toFile().getCanonicalPath());
        
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
