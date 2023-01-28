package xyz.wagyourtail.jsmacros.core.threads;

import java.util.HashSet;
import java.util.Set;

public class JsMacrosThreadPool {
    public Set<PoolThread> freeThreads = new HashSet<>();

    public final int minFreeThreads;
    public final int maxFreeThreads;

    public JsMacrosThreadPool() {
        this(2, 12);
    }

    public JsMacrosThreadPool(int minFreeThreads, int maxFreeThreads) {
        this.minFreeThreads = minFreeThreads;
        this.maxFreeThreads = maxFreeThreads;
        for (int i = 0; i < minFreeThreads; i++) {
            PoolThread t = new PoolThread();
            t.start();
            freeThreads.add(t);
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

        public void runTask(Runnable task) {
            synchronized (this) {
                this.task = task;
                notify();
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (this) {
                        while (task == null) {
                            wait();
                        }
                    }
                    task.run();
                } catch (Throwable ignored) {
                    interrupted();
                }
                synchronized (freeThreads) {
                    if (freeThreads.size() < minFreeThreads) {
                        PoolThread t = new PoolThread();
                        t.start();
                        freeThreads.add(t);
                    }
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
