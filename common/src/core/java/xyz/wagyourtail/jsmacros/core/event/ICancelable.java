package xyz.wagyourtail.jsmacros.core.event;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public interface ICancelable {
    
    void cancel();
    
    boolean isCanceled();
    
}
