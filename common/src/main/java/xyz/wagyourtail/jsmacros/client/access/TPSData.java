package xyz.wagyourtail.jsmacros.client.access;

public class TPSData {
    public final long recvTime;
    public final double tps;
    public TPSData(long time, double tps) {
        this.recvTime = time;
        this.tps = tps;
    }
}
