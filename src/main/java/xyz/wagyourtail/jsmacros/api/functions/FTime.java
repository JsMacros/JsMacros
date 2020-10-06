package xyz.wagyourtail.jsmacros.api.functions;

import java.util.List;

import xyz.wagyourtail.jsmacros.extensionbase.Functions;

/**
 * 
 * Functions for getting and using raw java classes, methods and functions.
 * 
 * An instance of this class is passed to scripts as the {@code time} variable.
 * 
 * @author Wagyourtail
 *
 */
public class FTime extends Functions {
    
    public FTime(String libName) {
        super(libName);
    }
    
    public FTime(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    /**
     * @return current time in MS.
     */
    public long time() {
        return System.currentTimeMillis();
    }
    
    /**
     * Sleeps the current thread for the specified time in MS.
     * 
     * @param millis
     * @throws InterruptedException
     */
    public void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
