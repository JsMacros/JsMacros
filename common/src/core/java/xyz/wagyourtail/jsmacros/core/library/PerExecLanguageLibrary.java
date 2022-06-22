package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

public class PerExecLanguageLibrary<T> extends BaseLibrary {
    protected final BaseScriptContext<T> ctx;
    protected final Class<? extends BaseLanguage<T>> language;
    
    public PerExecLanguageLibrary(BaseScriptContext<T> context, Class<? extends BaseLanguage<T>> language) {
        this.language = language;
        this.ctx = context;
    }
}
