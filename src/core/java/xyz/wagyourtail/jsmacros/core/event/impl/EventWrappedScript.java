package xyz.wagyourtail.jsmacros.core.event.impl;

import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @param <T>
 * @param <U>
 * @param <R>
 * @author Wagyourtail
 * @since 1.7.0
 */
@Event("WrappedScript")
public class EventWrappedScript<T, U, R> extends BaseEvent {
    public final T arg1;
    public final U arg2;

    public R result;

    public EventWrappedScript(Core<?, ?> runner, T arg1, U arg2) {
        super(runner);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public String toString() {
        return String.format("%s:{\"arg1\": %s, \"arg2\": %s}", this.getEventName(), arg1, arg2);
    }

    public void setReturnBoolean(boolean b) {
        result = (R) (Object) b;
    }

    public void setReturnInt(int i) {
        result = (R) (Object) i;
    }

    public void setReturnDouble(double d) {
        result = (R) (Object) d;
    }

    public void setReturnString(String s) {
        result = (R) (Object) s;
    }

    public void setReturnObject(Object o) {
        result = (R) (Object) o;
    }

}
