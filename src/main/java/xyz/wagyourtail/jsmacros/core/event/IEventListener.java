package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.core.language.ContextContainer;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;

import java.util.concurrent.Semaphore;

public interface IEventListener {
    
    ContextContainer<?> trigger(BaseEvent event);
}
