package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.text.Text;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface ICyclingButtonWidget<T> {
    
    void jsmacros_cycle(int amount);

    Text jsmacros_getTextValue();
    
    String jsmacros_toString(T val);
    
}
