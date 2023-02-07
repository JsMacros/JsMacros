package xyz.wagyourtail.jsmacros.core.threads;

import xyz.wagyourtail.SynchronizedWeakHashSet;

import java.util.HashSet;
import java.util.Set;

public class JsMacrosThreadPool {
    private final Set<PoolThread> freeThreads = new SynchronizedWeakHashSet<>();

    public final int minFreeThreads;
    public final int maxFreeThreads;

    public JsMacrosThreadPool() {
        this(4, 12);
    }

    public JsMacrosThreadPool(int minFreeThreads, int maxFreeThreads) {
        this.minFreeThreads = minFreeThreads;
        this.maxFreeThreads = maxFreeThreads;
        for (int i = 0; i < minFreeThreads; i++) {
            PoolThread t = new PoolThread();
            t.start();
        }
    }

    public void runTask(Runnable task) {
        synchronized (freeThreads) {
            if (freeThreads.isEmpty()) {
                // this shouldn't happen, I guess if called too fast...
                PoolThread t = new PoolThread();
                t.start();
                t.runTask(task);
            } else {
                PoolThread t = freeThreads.iterator().next();
                freeThreads.remove(t);
                t.runTask(task);
            }
        }
    }

    public class PoolThread extends Thread {
        private Runnable task;

        public PoolThread() {
            super("JsMacros Pool Thread");
            setDaemon(true);
        }

        @Override
        public synchronized void start() {
            super.start();
            freeThreads.add(this);
        }

        public void runTask(Runnable task) {
            synchronized (this) {
                this.setContextClassLoader(Thread.currentThread().getContextClassLoader());
                this.task = task;
                notify();
            }
        }

        @Override
        public void run() {
            while (task != null || freeThreads.contains(this)) {
                try {
                    synchronized (this) {
                        while (task == null) {
                            wait();
                        }
                    }
                    synchronized (freeThreads) {
                        if (freeThreads.size() < minFreeThreads) {
                            PoolThread t = new PoolThread();
                            t.start();
                        }
                    }
                    task.run();
                } catch (Throwable ignored) {
                    interrupted();
                }
                task = null;
                synchronized (freeThreads) {
                    if (freeThreads.size() >= maxFreeThreads) {
                        return;
                    }
                    freeThreads.add(this);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        JsMacrosThreadPool pool = new JsMacrosThreadPool();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            pool.runTask(() -> {
                System.out.println("Task " + finalI + " started");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Task " + finalI + " finished");
            });
        }
        Thread.sleep(5000);
    }
}
