package xyz.wagyourtail.jsmacros.core.extensions;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseWrappedException;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.nio.file.Path;
import java.util.Set;

public interface Extension {

    int getPriority();

    String getLanguageName();

    String getLanguageExtension();

    /**
     *
     * @return a single static instance of the language definition
     */
    BaseLanguage<?> getLanguage(Core<?, ?> runner);

    Set<Class<? extends BaseLibrary>> getLibraries();

    Set<Path> getDependencies();

    BaseWrappedException<?> wrapException(Throwable t);
}
