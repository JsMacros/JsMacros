package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;

public class PerExecLanguageLibrary<T> extends BaseLibrary {
    protected Class<? extends BaseLanguage> language;
    protected Object context;
    protected Thread thread;
    
    public PerExecLanguageLibrary(Class<? extends BaseLanguage> language, Object context, Thread thread) {
        this.language = language;
        this.context = context;
        this.thread = thread;
    }
}
