package xyz.wagyourtail.jsmacros.core.library.impl;

import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.PerExecLibrary;

/**
 * Functions for getting and using raw java classes, methods and functions.
 * <p>
 * An instance of this class is passed to scripts as the {@code Time} variable.
 *
 * @author Wagyourtail
 */
@Library("Time")
@SuppressWarnings("unused")
public class FTime extends PerExecLibrary {

    public FTime(BaseScriptContext<?> context) {
        super(context);
    }

    /**
     * @return current time in MS.
     */
    public long time() {
        return System.currentTimeMillis();
    }

    /**
     * Sleeps the current thread for the specified time in MS.
     *
     * @param millis
     * @throws InterruptedException
     */
    public void sleep(long millis) throws InterruptedException {
        ctx.wrapSleep(() -> Thread.sleep(millis));
    }

}
