package xyz.wagyourtail.jsmacros.js.language.impl;

public class WrappedThread {
    public final Thread thread;
    public final int priority;
    private boolean running = false;
    private boolean notDone = true;

    public WrappedThread(Thread thread, int priority) {
        this.thread = thread;
        this.priority = priority;
    }

    public synchronized void waitFor() throws InterruptedException {
        if (this.notDone) {
            this.wait();
        }
    }

    public void setRunning() {
        this.running = true;
    }

    public synchronized int release() {
        this.notDone = false;
        this.running = false;
        this.notifyAll();
        return priority;
    }

    public boolean isNotDone() {
        return this.notDone;
    }

    public boolean isRunning() {
        return this.running;
    }

}
