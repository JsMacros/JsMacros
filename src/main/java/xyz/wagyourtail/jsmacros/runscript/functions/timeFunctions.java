package xyz.wagyourtail.jsmacros.runscript.functions;

public class timeFunctions {
    public long time() {
        return System.currentTimeMillis();
    }
    
    public void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
