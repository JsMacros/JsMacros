package xyz.wagyourtail.jsmacros.core.event;

import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;

import java.util.concurrent.Semaphore;

public interface IEventListener {
    
    Pair<? extends ScriptContext<?>, Semaphore> trigger(BaseEvent event);
}
