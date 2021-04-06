package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.language.ContextContainer;

public abstract class PerExecLibrary extends BaseLibrary {
    protected ContextContainer<?> ctx;
    
    public PerExecLibrary(ContextContainer<?> context) {
        this.ctx = context;
    }
}
