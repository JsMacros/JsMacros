package xyz.wagyourtail.jsmacros.graal;

import com.google.common.collect.Sets;
import org.graalvm.polyglot.*;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.extensions.LanguageExtension;
import xyz.wagyourtail.jsmacros.core.extensions.LibraryExtension;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.graal.language.impl.GraalLanguageDefinition;
import xyz.wagyourtail.jsmacros.graal.language.impl.GuestExceptionSimplifier;
import xyz.wagyourtail.jsmacros.graal.library.impl.FWrapper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class GraalExtension implements LanguageExtension, LibraryExtension {

    private static GraalLanguageDefinition languageDefinition;

    @Override
    public String getExtensionName() {
        return "graal";
    }

    @Override
    public void init() {
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public ExtMatch extensionMatch(File fname) {
        try {
            if (GraalLanguageDefinition.engine.getLanguages().containsKey(Source.findLanguage(fname))) {
                if (fname.getName().contains(getExtensionName())) {
                    return ExtMatch.MATCH_WITH_NAME;
                }
                return ExtMatch.MATCH;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ExtMatch.NOT_MATCH;
    }

    @Override
    public String defaultFileExtension() {
        return "js";
    }

    @Override
    public BaseLanguage<?, ?> getLanguage(Core<?, ?> runner) {
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
                message = GuestExceptionSimplifier.simplifyException(ex);
                if (message == null) {
                    message = "UnknownGuestException";
                }
            }
            return new BaseWrappedException<>(ex, message, wrapLocation(((PolyglotException) ex).getSourceLocation()), frames.hasNext() ? internalWrap(frames.next(), frames) : null);
        }
        return null;
    }

    @Override
    public boolean isGuestObject(Object o) {
        return o instanceof Value;
    }

    private BaseWrappedException<?> internalWrap(PolyglotException.StackFrame current, Iterator<PolyglotException.StackFrame> frames) {
        if (current == null) {
            return null;
        }
        if (current.isGuestFrame()) {
            return new BaseWrappedException<>(current, " at " + current.getRootName(), wrapLocation(current.getSourceLocation()), frames.hasNext() ? internalWrap(frames.next(), frames) : null);
        }
        if (current.toHostFrame().getClassName().equals("org.graalvm.polyglot.Context") && current.toHostFrame().getMethodName().equals("eval")) {
            return null;
        }
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
