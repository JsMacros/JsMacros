package xyz.wagyourtail.jsmacros.core.threads;

import xyz.wagyourtail.SynchronizedWeakHashSet;

import java.util.Set;
import java.util.function.Consumer;

public class JsMacrosThreadPool {

    public Thread runTask(Runnable task) {
        Thread t = new Thread(task);
        t.start();
        return t;
    }

    public Thread runTask(Runnable task, Consumer<Thread> beforeRunTask) {
        Thread t = new Thread(task);
        beforeRunTask.accept(t);
        t.start();
        return t;
    }

}
