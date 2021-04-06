package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.ContextContainer;

public class PerExecLanguageLibrary extends BaseLibrary {
    protected final ContextContainer<?> ctx;
    protected final Class<? extends BaseLanguage<?>> language;
    
    public PerExecLanguageLibrary(ContextContainer<?> context, Class<? extends BaseLanguage<?>> language) {
        this.language = language;
        this.ctx = context;
    }
}
