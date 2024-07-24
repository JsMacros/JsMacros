package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.impl.EventWrappedScript;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

import java.util.function.Function;

public class WrappedScript<T, U, V> extends MethodWrapper<T, U, V, BaseScriptContext<?>> {
    private final Core<?, ?> runner;
    public final Function<BaseEvent, EventContainer<BaseScriptContext<?>>> f;
    public final boolean _async;

    public WrappedScript(Core<?, ?> runner, Function<BaseEvent, EventContainer<BaseScriptContext<?>>> f, boolean _async) {
        super();
        this.runner = runner;
        this.f = f;
        this._async = _async;
    }

    @Override
    public void accept(T t) {
        BaseEvent event = t instanceof BaseEvent ? (BaseEvent) t : new EventWrappedScript<>(runner, t, null);
        EventContainer<BaseScriptContext<?>> t1 = f.apply(event);
        if (!_async) {
            boolean joinedMain = runner.profile.checkJoinedThreadStack();
            if (joinedMain) {
                runner.profile.joinedThreadStack.add(t1.getLockThread());
            }
            EventLockWatchdog.startWatchdog(t1, null, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
            try {
                t1.awaitLock(() -> runner.profile.joinedThreadStack.remove(t1.getLockThread()));
            } catch (InterruptedException ignored) {
                runner.profile.joinedThreadStack.remove(t1.getLockThread());
            }
        }
    }

    @Override
    public void accept(T t, U u) {
        EventContainer<BaseScriptContext<?>> t1 = f.apply(new EventWrappedScript<>(runner, t, u));
        if (!_async) {
            boolean joinedMain = runner.profile.checkJoinedThreadStack();
            if (joinedMain) {
                runner.profile.joinedThreadStack.add(t1.getLockThread());
            }
            EventLockWatchdog.startWatchdog(t1, null, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
            try {
                t1.awaitLock(() -> runner.profile.joinedThreadStack.remove(t1.getLockThread()));
            } catch (InterruptedException ignored) {
                runner.profile.joinedThreadStack.remove(t1.getLockThread());
            }
        }
    }

    @Override
    public V apply(T t) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(runner, t, null));
        boolean joinedMain = runner.profile.checkJoinedThreadStack();
        if (joinedMain) {
            runner.profile.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, null, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> runner.profile.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            runner.profile.joinedThreadStack.remove(t1.getLockThread());
        }
        return e.result;
    }

    @Override
    public V apply(T t, U u) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(runner, t, u));
        boolean joinedMain = runner.profile.checkJoinedThreadStack();
        if (joinedMain) {
            runner.profile.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, null, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> runner.profile.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            runner.profile.joinedThreadStack.remove(t1.getLockThread());
        }
        return e.result;
    }

    @Override
    public boolean test(T t) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(runner, t, null));
        boolean joinedMain = runner.profile.checkJoinedThreadStack();
        if (joinedMain) {
            runner.profile.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, null, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> runner.profile.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            runner.profile.joinedThreadStack.remove(t1.getLockThread());
        }
        return (Boolean) e.result;
    }

    @Override
    public boolean test(T t, U u) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(runner, t, u));
        boolean joinedMain = runner.profile.checkJoinedThreadStack();
        if (joinedMain) {
            runner.profile.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, null, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> runner.profile.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            runner.profile.joinedThreadStack.remove(t1.getLockThread());
        }
        return (Boolean) e.result;
    }

    @Override
    public void run() {
        EventContainer<BaseScriptContext<?>> t1 = f.apply(new EventWrappedScript<>(runner, null, null));
        if (!_async) {
            boolean joinedMain = runner.profile.checkJoinedThreadStack();
            if (joinedMain) {
                runner.profile.joinedThreadStack.add(t1.getLockThread());
            }
            EventLockWatchdog.startWatchdog(t1, null, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
            try {
                t1.awaitLock(() -> runner.profile.joinedThreadStack.remove(t1.getLockThread()));
            } catch (InterruptedException ignored) {
                runner.profile.joinedThreadStack.remove(t1.getLockThread());
            }
        }
    }

    @Override
    public int compare(T o1, T o2) {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(runner, o1, null));
        boolean joinedMain = runner.profile.checkJoinedThreadStack();
        if (joinedMain) {
            runner.profile.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, null, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> runner.profile.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            runner.profile.joinedThreadStack.remove(t1.getLockThread());
        }
        return (Integer) e.result;
    }

    @Override
    public V get() {
        EventWrappedScript<T, U, V> e;
        EventContainer<BaseScriptContext<?>> t1 = f.apply(e = new EventWrappedScript<>(runner, null, null));
        boolean joinedMain = runner.profile.checkJoinedThreadStack();
        if (joinedMain) {
            runner.profile.joinedThreadStack.add(t1.getLockThread());
        }
        EventLockWatchdog.startWatchdog(t1, null, runner.config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            t1.awaitLock(() -> runner.profile.joinedThreadStack.remove(t1.getLockThread()));
        } catch (InterruptedException ignored) {
            runner.profile.joinedThreadStack.remove(t1.getLockThread());
        }
        return e.result;
    }

}
