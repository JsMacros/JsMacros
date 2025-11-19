package xyz.wagyourtail.jsmacros.client.tick;

import java.util.PriorityQueue;

/**
 * @author Wagyourtail
 * Ignore this xd
 */
public class TickSync {
    int tc = 0;
    final PriorityQueue<TickSyncInt> minHeap = new PriorityQueue<>();

    public synchronized void waitTick() throws InterruptedException {
        int tcc = tc;
        while (tc == tcc) {
            this.wait();
        }
    }

    public void waitTicks(int ticks) throws InterruptedException {
        final TickSyncInt ts = new TickSyncInt(tc + ticks);
        synchronized (minHeap) {
            minHeap.add(ts);
        }
        while (tc < ts.tick) {
            synchronized (ts) {
                ts.wait();
            }
        }
    }

    public synchronized void tick() {
        ++tc;
        this.notifyAll();
        synchronized (minHeap) {
            while (!minHeap.isEmpty() && minHeap.peek().tick <= tc) {
                TickSyncInt ts = minHeap.poll();
                synchronized (ts) {
                    ts.notifyAll();
                }
            }
        }
    }

    public static class TickSyncInt implements Comparable<TickSyncInt> {
        public final int tick;

        public TickSyncInt(int tick) {
            this.tick = tick;
        }

        @Override
        public int compareTo(TickSyncInt o) {
            return Integer.compare(tick, o.tick);
        }
    }

}
