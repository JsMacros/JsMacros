package xyz.wagyourtail.jsmacros.core.language.impl;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.Core;
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
            Core.instance.threadContext.entrySet().stream().filter(e -> e.getValue() == this).forEach(e -> e.getKey().interrupt());
            if (ctx != null) ctx.close(true);
            closed = true;
        }
    }
    
}
