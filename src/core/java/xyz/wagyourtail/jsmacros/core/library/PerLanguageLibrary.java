package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;

public abstract class PerLanguageLibrary extends BaseLibrary {
    protected Class<? extends BaseLanguage<?, ?>> language;

    public PerLanguageLibrary(Core<?, ?> runner, Class<? extends BaseLanguage<?, ?>> language) {
        super(runner);
        this.language = language;
    }

}
