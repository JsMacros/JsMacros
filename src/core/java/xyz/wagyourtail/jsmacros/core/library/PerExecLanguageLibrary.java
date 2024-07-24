package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

public class PerExecLanguageLibrary<U, T extends BaseScriptContext<U>> extends BaseLibrary {
    protected final T ctx;
    protected final Class<? extends BaseLanguage<U, T>> language;

    public PerExecLanguageLibrary(T context, Class<? extends BaseLanguage<U, T>> language) {
        super(context.runner);
        this.language = language;
        this.ctx = context;
    }

}
