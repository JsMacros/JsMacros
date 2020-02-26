package xyz.wagyourtail.luamacros;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.luamacros.config.ConfigManager;

public class LuaMacros implements ClientModInitializer {
	public static final String MOD_ID = "luamacros";
	public static ConfigManager config = new ConfigManager();
	
	@Override
	public void onInitializeClient() {
		config.loadConfig();
		
	}
	
	static public MinecraftClient getMinecraft() {
		return MinecraftClient.getInstance();
	}
	
}