package xyz.wagyourtail.jsmacros.stubs;

import com.google.common.collect.ImmutableList;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEventRegistry;

import java.util.List;

public class EventRegistryStub extends BaseEventRegistry {
    public EventRegistryStub(Core runner) {
        super(runner);
    }

    @Override
    public void addScriptTrigger(ScriptTrigger rawmacro) {
        throw new AssertionError("not implemented");
    }

    @Override
    public boolean removeScriptTrigger(ScriptTrigger rawmacro) {
        throw new AssertionError("not implemented");
    }

    @Override
    public List<ScriptTrigger> getScriptTriggers() {
        return ImmutableList.of();
    }

}
