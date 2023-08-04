package xyz.wagyourtail.jsmacros.core.threads;

import xyz.wagyourtail.SynchronizedWeakHashSet;

import java.util.Set;
import java.util.function.Consumer;

public class JsMacrosThreadPool {
    private final Set<PoolThread> freeThreads = new SynchronizedWeakHashSet<>();

    public final int minFreeThreads;
    public final int maxFreeThreads;

    public JsMacrosThreadPool() {
        this(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 3);
    }

    public JsMacrosThreadPool(int minFreeThreads, int maxFreeThreads) {
        this.minFreeThreads = minFreeThreads;
        this.maxFreeThreads = maxFreeThreads;
        for (int i = 0; i < minFreeThreads; i++) {
            PoolThread t = new PoolThread();
            t.start();
        }
        runTask(() -> {
            while (true) {
                synchronized (freeThreads) {
                    try {
                        freeThreads.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (freeThreads.size() < minFreeThreads) {
                        PoolThread t = new PoolThread();
                        t.start();
                    }
                }
                ;
            }
        });
    }

    public Thread runTask(Runnable task) {
        PoolThread t;
        synchronized (freeThreads) {
            if (freeThreads.isEmpty()) {
                // this shouldn't happen, I guess if called too fast...
                t = new PoolThread();
                t.startWithTask(task);
            } else {
                t = freeThreads.iterator().next();
                freeThreads.remove(t);
                t.runTask(task);
            }
        }
        return t;
    }

    public Thread runTask(Runnable task, Consumer<Thread> beforeRunTask) {
        PoolThread t;
        synchronized (freeThreads) {
            if (freeThreads.isEmpty()) {
                // this shouldn't happen, I guess if called too fast...
                t = new PoolThread();
                beforeRunTask.accept(t);
                t.startWithTask(task);
            } else {
                t = freeThreads.iterator().next();
                freeThreads.remove(t);
                freeThreads.notify();
                beforeRunTask.accept(t);
                t.runTask(task);
            }
        }
        return t;
    }

    public class PoolThread extends Thread {
        private Runnable task;

        public PoolThread() {
            super("JsMacros Pool Thread");
            setDaemon(true);
        }

        @Override
        public synchronized void start() {
            freeThreads.add(this);
            super.start();
        }

        public synchronized void startWithTask(Runnable task) {
            this.setContextClassLoader(Thread.currentThread().getContextClassLoader());
            this.task = task;
            start();
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
