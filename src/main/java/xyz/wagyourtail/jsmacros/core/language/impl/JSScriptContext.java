package xyz.wagyourtail.jsmacros.core.language.impl;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;

public class JSScriptContext extends ScriptContext<Context> {
    boolean closed = false;
    
    @Override
    public boolean isContextClosed() {
        return super.isContextClosed() || closed;
    }
    
    @Override
    public void closeContext() {
        if (context != null) {
            Context ctx = context.get();
            if (ctx != null) ctx.close(true);
            closed = true;
        }
    }
    
}
