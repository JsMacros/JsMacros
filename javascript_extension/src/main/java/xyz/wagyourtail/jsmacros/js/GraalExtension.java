package xyz.wagyourtail.jsmacros.js;

import com.google.common.collect.Sets;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.SourceSection;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.extensions.Extension;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.js.language.impl.GraalLanguageDefinition;
import xyz.wagyourtail.jsmacros.js.library.impl.FWrapper;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class GraalExtension implements Extension {

    private static GraalLanguageDefinition languageDefinition;
    private static String[] extensions;

    @Override
    public void init() {
        Thread t = new Thread(() -> {
            Context.Builder build = Context.newBuilder("js");
            Context con = build.build();
            con.eval("js", "console.log('js pre-loaded.')");
            con.close();
        });
        t.start();
        try {
            Core.getInstance().config.addOptions("js", JSConfig.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public String getLanguageImplName() {
        return "graal";
    }

    @Override
    public synchronized String[] getLanguageFileExtensions() {
        if (extensions == null) {
            String[] s = GraalLanguageDefinition.engine.getLanguages().keySet().toArray(new String[0]);
            // replace python with py
            for (int i = 0; i < s.length; i++) {
                s[i] = s[i].replace("python", "py");
            }
            // replace ruby with rb
            for (int i = 0; i < s.length; i++) {
                s[i] = s[i].replace("ruby", "rb");
            }
            if (s.length > 1) {
                // move js to the front
                for (int i = 0; i < s.length; i++) {
                    if (s[i].equals("js")) {
                        String tmp = s[0];
                        s[0] = s[i];
                        s[i] = tmp;
                        break;
                    }
                }
            }
            extensions = s;
        }
        return extensions;
    }

    @Override
    public BaseLanguage<?> getLanguage(Core<?, ?> runner) {
        if (languageDefinition == null) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(GraalExtension.class.getClassLoader());
            languageDefinition = new GraalLanguageDefinition(this, runner);
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        return languageDefinition;
    }

    @Override
    public Set<Class<? extends BaseLibrary>> getLibraries() {
        return Sets.newHashSet(FWrapper.class);
    }

    @Override
    public Set<URL> getDependencies() {
        if (System.getProperty("java.vm.vendor").toLowerCase(Locale.ROOT).contains("graalvm")) {
            return new HashSet<>();
        }
        return Extension.super.getDependencies();
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
