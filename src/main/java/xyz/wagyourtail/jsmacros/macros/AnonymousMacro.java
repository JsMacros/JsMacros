package xyz.wagyourtail.jsmacros.macros;

import java.util.Map;
import java.util.function.BiConsumer;

public class AnonymousMacro implements IEventListener {
    public BiConsumer<String, Map<String, Object>> onTrigger;
    public String creator = Thread.currentThread().getName();
    
    public AnonymousMacro(BiConsumer<String, Map<String, Object>> onTrigger) {
        this.onTrigger = onTrigger;
    }

    @Override
    public Thread trigger(String type, Map<String, Object> args) {
        if (onTrigger != null) onTrigger.accept(type, args);
        return null;
    }
    
    public String toString() {
        return String.format("AnonymousMacro:{\"creator\":\"%s\", \"function\":\"%s\"}", creator, onTrigger != null ? onTrigger.toString() : "null");
    }

}
