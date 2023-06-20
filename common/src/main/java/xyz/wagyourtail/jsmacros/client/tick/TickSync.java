package xyz.wagyourtail.jsmacros.client.tick;

/**
 * @author Wagyourtail
 * Ignore this xd
 */
public class TickSync {
    int tc = 0;

    public synchronized void waitTick() throws InterruptedException {
        int tcc = tc;
        while (tc == tcc) {
            this.wait();
        }
    }

    public synchronized void tick() {
        ++tc;
        this.notifyAll();
    }

}
