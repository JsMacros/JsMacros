package xyz.wagyourtail.jsmacros.core.language.impl;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

import java.io.File;

public class JSScriptContext extends BaseScriptContext<Context> {
    boolean closed = false;

    public JSScriptContext(BaseEvent event, File file) {
        super(event, file);
    }

    @Override
    public boolean isContextClosed() {
        return closed;
    }
    
    @Override
    public void closeContext() {
        super.closeContext();
        if (getContext() != null && !closed) {
            threads.forEach(Thread::interrupt);
            getContext().close(true);
            closed = true;
        }
    }
    
}
