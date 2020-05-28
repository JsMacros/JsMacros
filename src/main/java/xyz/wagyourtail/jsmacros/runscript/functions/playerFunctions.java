package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.reflector.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.runscript.classes.Inventory;

public class playerFunctions {
    public Inventory openInventory() {
        return new Inventory();
    }
    
    public ClientPlayerEntityHelper getPlayer() {
    	MinecraftClient mc = jsMacros.getMinecraft();
    	return new ClientPlayerEntityHelper(mc.player);
    }
}
