package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;

public abstract class PerLanguageLibrary extends BaseLibrary {
    protected Class<? extends BaseLanguage<?, ?>> language;
    
    public PerLanguageLibrary(Class<? extends BaseLanguage<?, ?>> language) {
        this.language = language;
    }
}
