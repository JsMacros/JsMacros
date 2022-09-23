package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.text.Text;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public interface ICyclingButtonWidget {
    
    void jsmacros_cycle(int amount);

    Text jsmacros_getTextValue();
    
}
