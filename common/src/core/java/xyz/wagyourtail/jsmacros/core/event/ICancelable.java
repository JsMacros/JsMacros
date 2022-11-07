package xyz.wagyourtail.jsmacros.core.event;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface ICancelable {
    
    void cancel();
    
    boolean isCanceled();
    
}
