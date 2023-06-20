package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

public abstract class PerExecLibrary extends BaseLibrary {
    protected BaseScriptContext<?> ctx;

    public PerExecLibrary(BaseScriptContext<?> context) {
        this.ctx = context;
    }

}
