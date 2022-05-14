package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import xyz.wagyourtail.jsmacros.client.config.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.BaseProfile;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.impl.EventWrappedScript;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

import java.util.function.Function;

public class WrappedScript<T, U, V> extends MethodWrapper<T, U, V, BaseScriptContext<?>> {
    private static final BaseProfile p = Core.getInstance().profile;
    public final Function<BaseEvent, EventContainer<BaseScriptContext<?>>> f;
    public final boolean _async;

    public WrappedScript(Function<BaseEvent, EventContainer<BaseScriptContext<?>>> f, boolean _async) {
        super();
        this.f = f;
        this._async = _async;
    }

    @Override
    public void accept(T t) {
        EventContainer<BaseScriptContext<?>> t1 = f.apply(new EventWrappedScript<>(t, null));
        if (!_async) {
            boolean joinedMain = p.checkJoinedThreadStack();
            if (joinedMain) {
                p.joinedThreadStack.add(t1.getLockThread());
            }
            EventLockWatchdog.startWatchdog(t1, event -> null, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
            try {
                t1.awaitLock(() -> p.joinedThreadStack.remove(t1.getLockThread()));
            } catch (InterruptedException ignored) {
                p.joinedThreadStack.remove(t1.getLockThread());
            }
        }
    }

    @Override
    public void accept(T t, U u) {
        EventContainer<BaseScriptContext<?>> t1 = f.apply(new EventWrappedScript<>(t, u));
        if (!_async) {
            boolean joinedMain = p.checkJoinedThreadStack();
            if (joinedMain) {
                p.joinedThreadStack.add(t1.getLockThread());
            }
            EventLockWatchdog.startWatchdog(t1, event -> null, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
            try {
                t1.awaitLock(() -> p.joinedThreadStack.remove(t1.getLockThread()));
            } catch (InterruptedException ignored) {
                p.joinedThreadStack.remove(t1.getLockThread());
            }
        }
    }

    @Override
    public V apply(T t) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(t, null));
        boolean joinedMain = p.checkJoinedThreadStack();
        if (joinedMain) {
            p.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, event -> null, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> p.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            p.joinedThreadStack.remove(t1.getLockThread());
        }
        return e.result;
    }

    @Override
    public V apply(T t, U u) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(t, u));
        boolean joinedMain = p.checkJoinedThreadStack();
        if (joinedMain) {
            p.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, event -> null, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> p.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            p.joinedThreadStack.remove(t1.getLockThread());
        }
        return e.result;
    }

    @Override
    public boolean test(T t) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(t, null));
        boolean joinedMain = p.checkJoinedThreadStack();
        if (joinedMain) {
            p.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, event -> null, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> p.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            p.joinedThreadStack.remove(t1.getLockThread());
        }
        return (Boolean) e.result;
    }

    @Override
    public boolean test(T t, U u) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(t, u));
        boolean joinedMain = p.checkJoinedThreadStack();
        if (joinedMain) {
            p.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, event -> null, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> p.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            p.joinedThreadStack.remove(t1.getLockThread());
        }
        return (Boolean) e.result;
    }

    @Override
    public void run() {
        EventContainer<BaseScriptContext<?>> t1 = f.apply(new EventWrappedScript<>(null, null));
        if (!_async) {
            boolean joinedMain = p.checkJoinedThreadStack();
            if (joinedMain) {
                p.joinedThreadStack.add(t1.getLockThread());
            }
            EventLockWatchdog.startWatchdog(t1, event -> null, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
            try {
                t1.awaitLock(() -> p.joinedThreadStack.remove(t1.getLockThread()));
            } catch (InterruptedException ignored) {
                p.joinedThreadStack.remove(t1.getLockThread());
            }
        }
    }

    @Override
    public int compare(T o1, T o2) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(o1, null));
        boolean joinedMain = p.checkJoinedThreadStack();
        if (joinedMain) {
            p.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, event -> null, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> p.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            p.joinedThreadStack.remove(t1.getLockThread());
        }
        return (Integer) e.result;
    }

    @Override
    public V get() {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(null, null));
        boolean joinedMain = p.checkJoinedThreadStack();
        if (joinedMain) {
            p.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, event -> null, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> p.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            p.joinedThreadStack.remove(t1.getLockThread());
        }
        return e.result;
    }

}
