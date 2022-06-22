package xyz.wagyourtail.jsmacros.js.language.impl;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

import java.io.File;

public class JSScriptContext extends BaseScriptContext<Context> {

    public JSScriptContext(BaseEvent event, File file) {
        super(event, file);
    }
    
    @Override
    public void closeContext() {
        super.closeContext();
        Context ctx = getContext();
        if (ctx != null) {
            ctx.close(true);
        }
    }

    @Override
    public boolean isMultiThreaded() {
        return false;
    }

}
