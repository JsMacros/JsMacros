package xyz.wagyourtail.jsmacros.js.language.impl;

import java.util.concurrent.locks.LockSupport;

public class WrappedThread {
    public final Thread thread;
    public final int priority;
    private boolean ready = false;

    public WrappedThread(Thread thread, int priority) {
        this.thread = thread;
        this.priority = priority;
    }

    public void waitUntilReady() throws InterruptedException {
        if (Thread.currentThread() != thread) throw new AssertionError("not the same thread");
        while (!ready) {
            LockSupport.park();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }
    }

    public void notifyReady() {
        ready = true;
        LockSupport.unpark(thread);
    }

}
