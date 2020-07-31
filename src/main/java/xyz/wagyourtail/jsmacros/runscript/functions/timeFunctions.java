package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.List;

public class timeFunctions extends Functions {
    
    public timeFunctions(String libName) {
        super(libName);
    }
    
    public timeFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    public long time() {
        return System.currentTimeMillis();
    }
    
    public void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
